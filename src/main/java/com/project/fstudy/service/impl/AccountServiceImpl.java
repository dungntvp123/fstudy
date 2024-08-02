package com.project.fstudy.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fstudy.data.constant.TimeConstant;
import com.project.fstudy.data.dto.request.*;
import com.project.fstudy.data.entity.*;
import com.project.fstudy.exception.*;
import com.project.fstudy.repository.*;
import com.project.fstudy.service.AccountManageService;
import com.project.fstudy.service.AuthService;
import com.project.fstudy.utils.EmailUtils;
import com.project.fstudy.utils.JwtUtils;
import com.project.fstudy.utils.PasswordUtils;
import com.project.fstudy.utils.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;


@Slf4j
@Service
public class AccountServiceImpl implements AuthService, AccountManageService {
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
    @Override
    public ResponseEntity<?> authenticate(UsernamePasswordAuthenticateRequestDto dto) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
        authenticationManager.authenticate(authentication);


        String token = jwtUtils.generateToken(dto.getUsername());
        return ResponseEntity.ok(token);
    }

    @Override
    public ResponseEntity<?> googleAuthenticate(GoogleAuthenticateRequestDto dto) {
        return null;
    }

    @Override
    public ResponseEntity<?> register(RegisterRequestDto dto) throws JsonProcessingException {
        List<String> violations = validationUtils.getViolationMessage(dto);

        if (!violations.isEmpty()) {
            String message = (new ObjectMapper()).writeValueAsString(violations);
            throw new InputConstraintViolationException(message);
        }

        Authority authority = authorityRepository.findById(2).orElseThrow(() -> {
            log.error("authority USER1 is missing in database");
            return new NoSuchElementException();
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
                    "Verify at: http://localhost:8080/api/auth/verify-account/" + verifyAccountToken.getAccountId();
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
            log.error("Unhandled Exception: {}", ex.getMessage());
            throw ex;
        }

    }

    @Override
    @Transactional
    public ResponseEntity<?> forgotPassword(ForgotPasswordRequestDto dto) {
        String otp = PasswordUtils.generatePassword();
        Account account = accountRepository.findByUserEmail(dto.getEmail())
                .orElseThrow(() -> new PersistentDataNotFound("Can't find account that match for email"));
        account.setPassword(passwordEncoder.encode(otp));
        account.setCredentialExpireTime(new Timestamp(System.currentTimeMillis() + TimeConstant.MINUTE * 10));
        try {
            accountRepository.save(account);
        } catch (Exception ex) {
            log.error("Encounter a exception: {}", ex.getMessage());
            throw ex;
        }
        try {
            emailUtils.sendSimpleEmail(dto.getEmail(), "Reset Password", "Your One time password: " + otp);
        } catch (Exception ex) {
            log.error("error while sending email: {}", ex.getMessage());
            throw ex;
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
            throw new DataValueConflictException("current password isn't match");
        }

        account.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        try {
            accountRepository.save(account);
        } catch (Exception ex) {
            log.error("Encounter a exception: {}", ex.getMessage());
            throw ex;
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> getRefreshToken() {
        UserDetails user = getPrincipal();
        if (user == null) {
            throw new InvalidAuthenticationPrincipalException();
        }

        if (accountRepository.findByUsername(user.getUsername()).isEmpty()) {
            throw new InvalidAuthenticationPrincipalException();
        }

        String token = jwtUtils.generateToken(user.getUsername());
        return ResponseEntity.ok(token);
    }

    @Override
    public ResponseEntity<?> verifyAccount(String token) {
        
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByUsername(username).orElse(null);
    }
}
