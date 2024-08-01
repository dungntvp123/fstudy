package com.project.fstudy.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fstudy.data.dto.request.*;
import com.project.fstudy.data.entity.Account;
import com.project.fstudy.data.entity.Authority;
import com.project.fstudy.data.entity.User;
import com.project.fstudy.exception.DataConstraintViolationException;
import com.project.fstudy.exception.InputConstraintViolationException;
import com.project.fstudy.repository.AccountRepository;
import com.project.fstudy.repository.AuthorityRepository;
import com.project.fstudy.repository.UserRepository;
import com.project.fstudy.service.AccountManageService;
import com.project.fstudy.service.AuthService;
import com.project.fstudy.utils.JwtUtils;
import com.project.fstudy.utils.ValidationUtils;
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
    @Transactional
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
                .build();
        try {
            accountRepository.save(account);
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = ex.getMostSpecificCause().getMessage();
            String message = "";
            if (errorMessage.contains("UK_email")) {
                message = "Email must be unique";
            } else if (errorMessage.contains("UK_username")) {
                message = "Username must be unique";
            } else {
                message = "Data integrity violation";
            }
            throw new DataConstraintViolationException(message);
        }
        String token = jwtUtils.generateToken(dto.getUsername());
        return ResponseEntity.ok(token);
    }

    @Override
    public ResponseEntity<?> forgotPassword(ForgotPasswordRequestDto dto) {
        return null;
    }

    @Override
    public ResponseEntity<?> updatePassword(Integer accountId, UpdatePasswordRequestDto dto) {
        return null;
    }

    @Override
    public ResponseEntity<?> getRefreshToken() {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByUsername(username).orElse(null);
    }
}
