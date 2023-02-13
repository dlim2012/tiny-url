package com.dlim2012.appuser.service;

import com.dlim2012.appuser.dto.AuthenticationRequest;
import com.dlim2012.appuser.dto.AuthenticationResponse;
import com.dlim2012.appuser.dto.RegisterRequest;
import com.dlim2012.appuser.entity.AppUser;
import com.dlim2012.appuser.repository.AppUserRepository;
import com.dlim2012.appuser.entity.AppUserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final AppUserRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService tokenService;

  public AuthenticationResponse register(RegisterRequest request) {
    Optional<AppUser> appUserOptional = repository.findByEmail(request.getEmail());
    AppUser appUser;
    if (appUserOptional.isPresent()){
      appUser = appUserOptional.get();
      if (request.getFirstname().equals(appUser.getFirstname())
              && request.getLastname().equals(appUser.getLastname())){
        if (!passwordEncoder.matches(request.getPassword(), appUser.getPassword())){
          throw new IllegalStateException("User exists but entered a wrong password");
        }
      } else {
        throw new IllegalStateException("Email taken");
      }
    } else {
      appUser = AppUser.builder()
              .firstname(request.getFirstname())
              .lastname(request.getLastname())
              .email(request.getEmail())
              .password(passwordEncoder.encode(request.getPassword()))
              .appUserRole(AppUserRole.USER)
              .availableShortUrl(1000)
              .build();
      repository.save(appUser);
    }
    var jwtToken = tokenService.generateToken(appUser);
    return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var appUser = repository.findByEmail(request.getEmail())
        .orElseThrow();
    return authenticationResponse(appUser);
  }

  public AuthenticationResponse authenticationResponse(UserDetails userDetails){
    var jwtToken = tokenService.generateToken(userDetails);
    return AuthenticationResponse.builder()
            .token(jwtToken)
            .build();
  }
}
