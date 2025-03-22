package com.pos.be.security.controller;

import com.pos.be.entity.user.User;
import com.pos.be.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtDecoder jwtDecoder;

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest loginRequest
    ) {
        try {
            return authenticationService.authenticate(loginRequest);
        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Authorization header format");
            }

            String token = authHeader.substring(7); // Remove "Bearer " prefix
            Jwt decodedJwt = jwtDecoder.decode(token);

            return ResponseEntity.ok()
                    .body("Token is valid. Subject: " + decodedJwt.getSubject());
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token: " + e.getMessage());
        }
    }

    @PostMapping("/signup")
    public String register(@RequestBody User user) throws RoleNotFoundException {
        return authenticationService.register(user);
    }

    public void printCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            System.out.println("Authenticated user: " + authentication.getName());
            System.out.println("Authorities: " + authentication.getAuthorities());
            System.out.println("User details: " + authentication.getDetails());
        } else {
            System.out.println("No user is authenticated.");
        }
    }
}
