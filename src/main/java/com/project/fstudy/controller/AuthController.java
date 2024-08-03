package com.project.fstudy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.fstudy.data.dto.request.*;
import com.project.fstudy.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody UsernamePasswordAuthenticateRequestDto dto) {
        return authService.authenticate(dto);
    }
    @PostMapping("/authenticate/google")
    public ResponseEntity<?> googleAuthenticate(@RequestBody GoogleAuthenticateRequestDto dto) throws GeneralSecurityException, IOException {
        return authService.googleAuthenticate(dto);
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto dto) throws JsonProcessingException {
        return authService.register(dto);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDto dto) {
        return authService.forgotPassword(dto);
    }
    @PatchMapping("/update-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequestDto dto) {
        return authService.updatePassword(dto);
    }
    @GetMapping("/get-refresh-token")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getRefreshToken() {
        return authService.getRefreshToken();
    }
    @GetMapping("/verify-account/{token}")
    public ResponseEntity<?> verifyAccount(@PathVariable("token") String token) {
        return authService.verifyAccount(token);
    }
}
