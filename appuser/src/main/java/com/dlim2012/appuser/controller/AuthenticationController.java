package com.dlim2012.appuser.controller;

import com.dlim2012.appuser.dto.AuthenticationRequest;
import com.dlim2012.appuser.dto.AuthenticationResponse;
import com.dlim2012.appuser.dto.RegisterRequest;
import com.dlim2012.appuser.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
@Slf4j
@CrossOrigin
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/api/v1/auth/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request,
            HttpServletResponse httpServletResponse
    ) {
        log.info("Register requested from {}", request.getEmail());
        AuthenticationResponse authenticationResponse;
        try{
            authenticationResponse = service.register(request);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(
                    new AuthenticationResponse("", e.getMessage())
            );
        }
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
        log.info("Authenticate requested from {}", request.getEmail());
        AuthenticationResponse authenticationResponse;
        try{
            authenticationResponse = service.authenticate(request);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(
                    new AuthenticationResponse("", e.getMessage())
            );
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("jwt_authentication", authenticationResponse.getToken());
        final Cookie cookie = new Cookie("auth", authenticationResponse.getToken());
        httpServletResponse.addCookie(cookie);
        return ResponseEntity.ok().headers(responseHeaders).body(authenticationResponse);
    }
}
