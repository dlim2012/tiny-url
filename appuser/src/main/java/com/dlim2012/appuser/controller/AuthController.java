package com.dlim2012.appuser.controller;

import com.dlim2012.appuser.dto.*;
import com.dlim2012.appuser.service.AuthenticationService;
import com.dlim2012.appuser.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    private final JwtService tokenService;
    private final AuthenticationService service;

    @PostMapping("/token")
    public String token (Authentication authentication){
        log.debug("Token requested for user: '{}'", authentication.getName());
        String token = tokenService.generateToken(authentication);
        log.debug("Token granted {} for user {}", token, authentication.getName());
        return token;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        log.info("Register requested from {}", request.getEmail());
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        log.info("AUthenticate requested from {}", request.getEmail());
        return ResponseEntity.ok(service.authenticate(request));
    }
}
