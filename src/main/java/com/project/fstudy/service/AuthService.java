package com.project.fstudy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.fstudy.data.dto.request.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {
    public ResponseEntity<?> authenticate(UsernamePasswordAuthenticateRequestDto dto);
    public ResponseEntity<?> googleAuthenticate(GoogleAuthenticateRequestDto dto);
    public ResponseEntity<?> register(RegisterRequestDto dto) throws JsonProcessingException;
    public ResponseEntity<?> forgotPassword(ForgotPasswordRequestDto dto);
    public ResponseEntity<?> updatePassword(Integer accountId, UpdatePasswordRequestDto dto);
    public ResponseEntity<?> getRefreshToken();

}
