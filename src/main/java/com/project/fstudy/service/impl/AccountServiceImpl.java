package com.project.fstudy.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fstudy.common.HasSecurity;
import com.project.fstudy.data.constant.TimeConstant;
import com.project.fstudy.data.dto.request.*;
import com.project.fstudy.data.entity.*;
import com.project.fstudy.exception.*;
import com.project.fstudy.repository.*;
import com.project.fstudy.service.AccountManageService;
import com.project.fstudy.service.AuthService;
import com.project.fstudy.utils.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;

import java.time.Instant;
import java.util.*;


@Slf4j
@Service
public class AccountServiceImpl implements AuthService, AccountManageService, HasSecurity {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private ValidationUtils validationUtils;
    @Autowired
    private EmailUtils emailUtils;
    @Autowired
    private VerifyAccountTokenRepository verifyAccountTokenRepository;
    @Autowired
    private GoogleUtils googleUtils;
    @Override

    public ResponseEntity<?> authenticate(UsernamePasswordAuthenticateRequestDto dto) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
        authenticationManager.authenticate(authentication);


        String token = jwtUtils.generateToken(dto.getUsername());
        return ResponseEntity.ok(token);
    }

    @Override
    public ResponseEntity<?> googleAuthenticate(GoogleAuthenticateRequestDto dto) throws GeneralSecurityException, IOException {
        Map<String, String> userInfo = googleUtils.getUserInfo(dto.getToken());
        String token;
        if (accountRepository.existsByUserEmail(userInfo.get("email"))) {
            Account account = accountRepository.findByUserEmail(userInfo.get("email")).get();
            token = jwtUtils.generateToken(account.getUsername());
        } else {
            Authority authority = authorityRepository.findById(2).orElseThrow(() -> {
                log.error("authority USER1 is missing in database");
                return new ServerUnhandledErrorException("Can't find user Authority");
            });
            String[] names = userInfo.get("name").split(" ");
            User user = User.builder()
                    .email(userInfo.get("email"))
                    .firstName(names[0])
                    .lastName(names[1])
                    .build();

            Account account = Account.builder()
                    .username(userInfo.get("username"))
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .authorities(Set.of(authority))
                    .user(user)
                    .isEnabled(true)
                    .isLocked(false)
                    .credentialExpireTime(new Timestamp(System.currentTimeMillis() + TimeConstant.MONTH))
                    .build();

            try {
                accountRepository.save(account);
                token = jwtUtils.generateToken(account.getUsername());
            } catch (Exception ex) {
                log.error("Saving account exception: {}", ex.getMessage());
                throw new ServerUnhandledErrorException(ex.getMessage());
            }
        }
        return ResponseEntity.ok(token);
    }

    @Override
    @Transactional
    public ResponseEntity<?> register(RegisterRequestDto dto) throws JsonProcessingException {
        List<String> violations = validationUtils.getViolationMessage(dto);

        if (!violations.isEmpty()) {
            String message = (new ObjectMapper()).writeValueAsString(violations);
            throw new InputConstraintViolationException(message);
        }

        Authority authority = authorityRepository.findById(2).orElseThrow(() -> {
            log.error("authority USER1 is missing in database");
            return new ServerUnhandledErrorException("can't find user Authority");
        });
        User user = User.builder()
                .email(dto.getEmail())
                .firstName(dto.getUsername())
                .build();
        Account account = Account.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .authorities(Set.of(authority))
                .user(user)
                .isEnabled(false)
                .isLocked(false)
                .credentialExpireTime(new Timestamp(System.currentTimeMillis() + TimeConstant.MONTH))
                .build();
        try {
            accountRepository.save(account);
            VerifyAccountToken verifyAccountToken = VerifyAccountToken.builder()
                    .accountId(account.getId())
                    .expireTime(Instant.ofEpochMilli(System.currentTimeMillis() + TimeConstant.MINUTE * 10))
                    .build();
            verifyAccountTokenRepository.save(verifyAccountToken);
            String emailContent =
                    "Verify at: http://localhost:8080/api/auth/verify-account/" + verifyAccountToken.getId();
            emailUtils.sendSimpleEmail(dto.getEmail(), "Verify account", emailContent);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = ex.getMostSpecificCause().getMessage();
            log.info("message: {}", errorMessage);
            String message = "";
            if (errorMessage.contains("UK_email")) {
                message = "Email must be unique";
            } else if (errorMessage.contains("UK_username")) {
                message = "Username must be unique";
            } else {
                message = "Data integrity violation";
            }
            throw new DataConstraintViolationException(message);
        } catch (Exception ex) {
            log.error("Saving account exception: {}", ex.getMessage());
            throw new ServerUnhandledErrorException(ex.getMessage());
        }

    }

    @Override
    @Transactional
    public ResponseEntity<?> forgotPassword(ForgotPasswordRequestDto dto) {
        String otp = PasswordUtils.generatePassword();
        Account account = accountRepository.findByUserEmail(dto.getEmail())
                .orElseThrow(() -> new PersistentDataNotFoundException("Can't find account that match for email"));
        account.setPassword(passwordEncoder.encode(otp));
        account.setCredentialExpireTime(new Timestamp(System.currentTimeMillis() + TimeConstant.MINUTE * 10));
        try {
            accountRepository.save(account);
        } catch (Exception ex) {
            log.error("Saving account exception: {}", ex.getMessage());
            throw new ServerUnhandledErrorException(ex.getMessage());
        }
        try {
            emailUtils.sendSimpleEmail(dto.getEmail(), "Reset Password", "Your One time password: " + otp);
        } catch (Exception ex) {
            log.error("Error while sending email: {}", ex.getMessage());
            throw new ServerUnhandledErrorException(ex.getMessage());
        }

        return ResponseEntity.noContent().build();
    }

    @Override
    @Transactional
    public ResponseEntity<?> updatePassword(UpdatePasswordRequestDto dto) {
        UserDetails user = getPrincipal();
        if (user == null) {
            throw new InvalidAuthenticationPrincipalException();
        }
        Account account = accountRepository.findByUsername(user.getUsername())
                .orElseThrow(InvalidAuthenticationPrincipalException::new);

        if (passwordEncoder.matches(dto.getOldPassword(), account.getPassword())) {
            throw new DataValueConflictException("Current password isn't match");
        }

        account.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        try {
            accountRepository.save(account);
        } catch (Exception ex) {
            log.error("Saving account exception: {}", ex.getMessage());
            throw new ServerUnhandledErrorException(ex.getMessage());
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> getRefreshToken() {
        UserDetails user = getPrincipal();
        if (user == null) {
            throw new InvalidAuthenticationPrincipalException();
        }

        if (accountRepository.existsByUsername(user.getUsername())) {
            throw new InvalidAuthenticationPrincipalException();
        }

        String token = jwtUtils.generateToken(user.getUsername());
        return ResponseEntity.ok(token);
    }

    @Override
    @Transactional
    public ResponseEntity<?> verifyAccount(String tokenId) {
        VerifyAccountToken verifyAccountToken = verifyAccountTokenRepository.findById(tokenId)
                .orElseThrow(() -> new PersistentDataNotFoundException("Can't find match verify token"));
        Account account = accountRepository.findById(verifyAccountToken.getAccountId())
                .orElseThrow(() -> new PersistentDataNotFoundException("Can't find account"));

        account.setEnabled(true);
        try {
            accountRepository.save(account);
            verifyAccountTokenRepository.delete(verifyAccountToken);
            String token = jwtUtils.generateToken(account.getUsername());
            return ResponseEntity.ok(token);
        } catch (Exception ex) {
            log.error("Saving account exception: {}", ex.getMessage());
            throw new ServerUnhandledErrorException(ex.getMessage());
        }

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByUsername(username).orElse(null);
    }
}
