package com.project.fstudy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.fstudy.common.HasSecurity;
import com.project.fstudy.data.dto.request.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface AuthService extends UserDetailsService {
    public ResponseEntity<?> authenticate(UsernamePasswordAuthenticateRequestDto dto);
    public ResponseEntity<?> googleAuthenticate(GoogleAuthenticateRequestDto dto) throws GeneralSecurityException, IOException;
    public ResponseEntity<?> register(RegisterRequestDto dto) throws JsonProcessingException;
    public ResponseEntity<?> forgotPassword(ForgotPasswordRequestDto dto);
    public ResponseEntity<?> updatePassword(UpdatePasswordRequestDto dto);
    public ResponseEntity<?> getRefreshToken();
    public ResponseEntity<?> verifyAccount(String token);

}
