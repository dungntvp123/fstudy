package com.project.fstudy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.fstudy.data.dto.request.RegisterRequestDto;
import com.project.fstudy.data.dto.request.UsernamePasswordAuthenticateRequestDto;
import com.project.fstudy.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody UsernamePasswordAuthenticateRequestDto dto) {
        return authService.authenticate(dto);
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto dto) throws JsonProcessingException {
        return authService.register(dto);
    }
}
