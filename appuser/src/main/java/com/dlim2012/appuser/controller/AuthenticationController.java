package com.dlim2012.appuser.controller;

import com.dlim2012.appuser.dto.AuthenticationRequest;
import com.dlim2012.appuser.dto.AuthenticationResponse;
import com.dlim2012.appuser.dto.Main;
import com.dlim2012.appuser.dto.RegisterRequest;
import com.dlim2012.appuser.service.AuthenticationService;
import com.dlim2012.appuser.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
@Slf4j
@CrossOrigin
public class AuthenticationController {

    private final JwtService tokenService;
    private final AuthenticationService service;

//    @PostMapping("/token")
//    public String token (Authentication authentication){
//        log.debug("Token requested for user: '{}'", authentication.getName());
//        String token = tokenService.generateToken(authentication);
//        log.debug("Token granted {} for user {}", token, authentication.getName());
//        return token;
//    }
    @GetMapping("/api")
    public List<Main> getMain(HttpServletRequest request) {
        log.info("get main");
        return service.getMain(request);
    }

    @PostMapping("/api/v1/auth/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request,
            HttpServletResponse httpServletResponse
    ) {
        log.info("Register requested from {}", request.getEmail());
        AuthenticationResponse authenticationResponse = service.register(request);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("jwt_authentication", authenticationResponse.getToken());
        final Cookie cookie = new Cookie("auth", authenticationResponse.getToken());
        httpServletResponse.addCookie(cookie);
        return ResponseEntity.ok().headers(responseHeaders).body(authenticationResponse);
    }

    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse httpServletResponse
    ) {
        log.info("AUthenticate requested from {}", request.getEmail());
        AuthenticationResponse authenticationResponse = service.authenticate(request);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("jwt_authentication", authenticationResponse.getToken());
        final Cookie cookie = new Cookie("auth", authenticationResponse.getToken());
        httpServletResponse.addCookie(cookie);
        return ResponseEntity.ok().headers(responseHeaders).body(authenticationResponse);
//        return ResponseEntity.ok(service.authenticate(request));
    }
}
