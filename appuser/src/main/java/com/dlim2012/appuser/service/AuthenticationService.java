package com.dlim2012.appuser.service;

import com.dlim2012.appuser.dto.AuthenticationRequest;
import com.dlim2012.appuser.dto.AuthenticationResponse;
import com.dlim2012.appuser.dto.Main;
import com.dlim2012.appuser.dto.RegisterRequest;
import com.dlim2012.appuser.entity.AppUser;
import com.dlim2012.appuser.entity.AppUserRole;
import com.dlim2012.appuser.repository.AppUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
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
          throw new IllegalStateException("Wrong password");
        }
      } else {
        throw new IllegalStateException("Email taken");
      }
    } else {
      if (!isValidEmail(request.getEmail())){
        throw new IllegalStateException("Invalid Email");
      }
      appUser = AppUser.builder()
              .firstname(request.getFirstname())
              .lastname(request.getLastname())
              .email(request.getEmail())
              .password(passwordEncoder.encode(request.getPassword()))
              .appUserRole(AppUserRole.USER)
              .availableShortUrl(1000)
              .appUserCreatedAt(LocalDateTime.now())
              .build();
      repository.save(appUser);
    }
    return authenticationResponse(appUser);
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            )
    );
    var appUser = repository.findByEmail(request.getEmail())
            .orElseThrow(() ->
                    new IllegalStateException("Username not found")
            );
    return authenticationResponse(appUser);
  }

  public AuthenticationResponse authenticationResponse(UserDetails userDetails){
    var jwtToken = tokenService.generateToken(userDetails);
    return AuthenticationResponse.builder()
            .token(jwtToken)
            .message("")
            .build();
  }

  public boolean isValidEmail(String email){
    if (email.length() < 8) {
      throw new IllegalStateException(
              String.format("Email %s is too short (minimum length 8)", email));
    }
    if (!Pattern.compile("^(.+)@(.+)$").matcher(email).matches()){
      throw new IllegalStateException(String.format("%s is not a valid email address", email));
    }
    return true;
  }


  public List<Main> getMain(HttpServletRequest request) {
    List<Main> mains = new ArrayList<>();
    List<AppUser> appUsers = repository.findAll();
    for (AppUser appUser: appUsers){
      mains.add(
              new Main(appUser.getEmail())
      );
    }
    return mains;
  }

}
