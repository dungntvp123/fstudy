package com.project.fstudy.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fstudy.common.ant.LogExecutionTime;
import com.project.fstudy.common.cls.BodyFormatter;
import com.project.fstudy.common.itf.HasSecurity;
import com.project.fstudy.data.constant.TimeConstant;
import com.project.fstudy.data.dto.request.*;
import com.project.fstudy.data.dto.request.criteria.AccountCriteria;
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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
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
    @Autowired
    private RawAccountRepository rawAccountRepository;

    @Override
    public ResponseEntity<?> authenticate(UsernamePasswordAuthenticateRequestDto dto) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
        try {
            authenticationManager.authenticate(authentication);
        } catch (AccountExpiredException ex) {
            Timestamp expireTime = accountRepository.findAccountExpiredTimeByUsername(dto.getUsername()).get();
            throw new AccountExpiredException(expireTime.toString());
        } catch (Exception ex) {
            log.error("Authenticate error: {}", ex.getMessage());
            throw ex;
        }

        String token = jwtUtils.generateToken(dto.getUsername());
        return ResponseEntity.ok(BodyFormatter.format(token, null));
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
                    .email(userInfo.get("email"))
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .authorities(Set.of(authority))
                    .user(user)
                    .accountExpiredTime(new Timestamp(TimeConstant.NOW - 1))
                    .isEnabled(true)
                    .isLocked(false)
                    .credentialExpiredTime(new Timestamp(TimeConstant.NOW + TimeConstant.MONTH))
                    .build();

            try {
                accountRepository.save(account);
                token = jwtUtils.generateToken(account.getUsername());
            } catch (Exception ex) {
                log.error("Saving account exception: {}", ex.getMessage());
                throw new ServerUnhandledErrorException(ex.getMessage());
            }
        }
        return ResponseEntity.ok(BodyFormatter.format(token, null));
    }

    @Override
    @LogExecutionTime
    public ResponseEntity<?> register(RegisterRequestDto dto) throws JsonProcessingException {

        List<String> violations = validationUtils.getViolationMessage(dto);

        if (!violations.isEmpty()) {
            String message = (new ObjectMapper()).writeValueAsString(violations);
            throw new InputConstraintViolationException(message);
        }

        try {
            RawAccount rawAccount = RawAccount.builder()
                    .email(dto.getEmail())
                    .password(dto.getPassword())
                    .username(dto.getUsername())
                    .expiredTime(Instant.ofEpochMilli(TimeConstant.NOW + TimeConstant.MINUTE * 10))
                    .build();
            rawAccountRepository.save(rawAccount);

            try {
                VerifyAccountToken verifyAccountToken = VerifyAccountToken.builder()
                        .accountId(rawAccount.getId())
                        .expiredTime(Instant.ofEpochMilli(TimeConstant.NOW + TimeConstant.MINUTE * 10))
                        .build();
                verifyAccountTokenRepository.save(verifyAccountToken);
                String emailContent =
                        "Verify at: http://localhost:8080/api/auth/verify-account/" + verifyAccountToken.getId();
                emailUtils.sendSimpleEmail(dto.getEmail(), "Verify account", emailContent);
                return ResponseEntity.noContent().build();
            } catch (Exception ex) {
                rawAccountRepository.delete(rawAccount);
                log.error("Saving account verify token exception: {}", ex.getMessage());
                throw new ServerUnhandledErrorException(ex.getMessage());
            }
        } catch (DuplicateKeyException ex) {

            if (ex.getMessage().contains("username")) {
                throw new DataConstraintViolationException("Username already exists");
            } else if (ex.getMessage().contains("email")) {
                throw new DataConstraintViolationException("Email already exists");
            } else {
                throw new DataConstraintViolationException("Data constraint violation");
            }
        } catch (Exception ex) {
            log.error("Saving raw account exception: {}", ex.getMessage());
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
        account.setCredentialExpiredTime(new Timestamp(TimeConstant.NOW + TimeConstant.MINUTE * 10));
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
        return ResponseEntity.ok(BodyFormatter.format(token, null));
    }

    @Override
    @Transactional
    @LogExecutionTime
    public ResponseEntity<?> verifyAccount(String tokenId) {
        VerifyAccountToken verifyAccountToken = verifyAccountTokenRepository.findByIdAndExpiredTimeAfter(tokenId, Instant.now())
                .orElseThrow(() -> new PersistentDataNotFoundException("Account verify token expired or not found"));
        RawAccount rawAccount = rawAccountRepository.findById(verifyAccountToken.getAccountId())
                .orElseThrow(() -> new PersistentDataNotFoundException("Can't find account"));

        Authority authority = authorityRepository.findById(2).orElseThrow(() -> {
            log.error("authority USER1 is missing in database");
            return new ServerUnhandledErrorException("can't find user Authority");
        });
        User user = User.builder()
                .email(rawAccount.getEmail())
                .firstName(rawAccount.getUsername())
                .build();
        Account account = Account.builder()
                .username(rawAccount.getUsername())
                .email(rawAccount.getEmail())
                .password(passwordEncoder.encode(rawAccount.getPassword()))
                .authorities(Set.of(authority))
                .user(user)
                .accountExpiredTime(new Timestamp(TimeConstant.NOW -1))
                .isEnabled(true)
                .isLocked(false)
                .credentialExpiredTime(new Timestamp(TimeConstant.NOW + TimeConstant.MONTH))
                .build();
        try {
            accountRepository.save(account);
            String token = jwtUtils.generateToken(account.getUsername());
            return ResponseEntity.ok(BodyFormatter.format(token, null));
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(""));
    }

    @Override
    public ResponseEntity<?> getAccount(AccountCriteria criteria) {
        Specification<Account> specification = hasAccountExpired(criteria.getIsLocked())
                .and(hasEmailLike(criteria.getSearchKey()).or(hasUsernameLike(criteria.getSearchKey())))
                .and(hasLocked(criteria.getIsLocked()));
        PageRequest pageRequest = (criteria.getSortField() != null ?
                inOrder(!criteria.isDescend(), criteria.getSortField(), criteria.getPageIndex()) :
                inDefault(criteria.getPageIndex()));

        Page<Account> accountPage = accountRepository.findAll(specification, pageRequest);

        return ResponseEntity.ok(BodyFormatter.format(accountPage, null));
    }

    @Override
    public ResponseEntity<?> getUserActionLog() {
        return null;
    }

    @Override
    public ResponseEntity<?> adjustLockAccount(Integer accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new PersistentDataNotFoundException("Can't find account"));

        account.setLocked(!account.isLocked());
        try {
            accountRepository.save(account);
        } catch (Exception ex) {
            log.error("Saving account exception: {}", ex.getMessage());
            throw new ServerUnhandledErrorException(ex.getMessage());
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> setExpireToAccount(Integer accountId, Integer type) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new PersistentDataNotFoundException("Can't find account"));

        switch (type) {
            case 0 -> account.setAccountExpiredTime(new Timestamp(TimeConstant.NOW - 1));
            case 1 -> account.setAccountExpiredTime(new Timestamp(TimeConstant.NOW + TimeConstant.DAY));
            case 2 -> account.setAccountExpiredTime(new Timestamp(TimeConstant.NOW + 3 * TimeConstant.DAY));
            case 3 -> account.setAccountExpiredTime(new Timestamp(TimeConstant.NOW + TimeConstant.WEEK));
            case 4 -> account.setAccountExpiredTime(new Timestamp(TimeConstant.NOW + TimeConstant.MONTH));
            default -> {
                log.warn("type not exist: {}", type);
                throw new InputConstraintViolationException("there is no type of " + type);
            }
        }

        try {
            accountRepository.save(account);
        } catch (Exception ex) {
            log.error("Saving account exception: {}", ex.getMessage());
            throw new ServerUnhandledErrorException(ex.getMessage());
        }

        return ResponseEntity.noContent().build();
    }
}
