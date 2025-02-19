package com.pos.be.security;

import com.pos.be.entity.user.User;
import com.pos.be.repository.user.UserRepository;
import com.pos.be.security.controller.AuthResponse;
import com.pos.be.security.controller.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.management.relation.RoleNotFoundException;
import java.time.Instant;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final ApplicationContext applicationContext;
    private final AuthenticationManager authenticationManager;

    public AuthResponse authenticate(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = this.createToken(authentication);

            return new AuthResponse(loginRequest.getUsername(), token);
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid username or password");
        } catch (Exception e) {
            System.err.println("Unexpected error during authentication: " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during login", e);
        }
    }




    public String register(User user) throws RoleNotFoundException {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists!");
        }

        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new RoleNotFoundException("Default role not found");// TODO: 11/10/2024 remove default role
        }

        userRepository.save(user);
        return "User registered successfully!";
    }


    public String createToken(Authentication authentication) {
        var claims = JwtClaimsSet.builder()
                .issuer("http://localhost:8080/pos")
                .issuedAt(Instant.now())
                .subject(authentication.getName())
                .expiresAt(Instant.now().plusSeconds(15 * 60 * 60))
                .claim("roles", createClaims(authentication))
                .build();

        JwtEncoder jwtEncoder = applicationContext.getBean("jwtEncoder", JwtEncoder.class);

        return jwtEncoder.encode(
                JwtEncoderParameters.from(claims)
        ).getTokenValue();
    }

    private Object createClaims(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(
                        GrantedAuthority::getAuthority
                )
                .collect(Collectors.toList());
    }
}
