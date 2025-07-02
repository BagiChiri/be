package com.pos.be.security.controller;

import com.pos.be.entity.user.Role;
import com.pos.be.entity.user.User;
import com.pos.be.repository.user.RoleRepository;
import com.pos.be.repository.user.UserRepository;
import com.pos.be.security.AuthenticationService;
import com.pos.be.security.rbac.Roles;
import com.pos.be.security.rbac.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ApplicationContext applicationContext;

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest loginRequest
    ) {
        try {
            return authenticationService.authenticate(loginRequest);
        } catch (BadCredentialsException e) {
            throw e;
        } catch (DisabledException e) {
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
    // Update AuthenticationService.java
    public String register(@RequestBody User user) throws RoleNotFoundException {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists!");
        }

        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);

        // Assign default role if none provided
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role defaultRole = roleRepository.findByName(Roles.CUSTOMER)
                    .orElseThrow(() -> new RoleNotFoundException("Default role not found"));
            user.getRoles().add(defaultRole);
        }
        User loogedInUser = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        // For admin registration (should be protected)
        if (loogedInUser.getRoles().stream().anyMatch(r -> r.getName().equals(Roles.ADMIN))) {
            if (!SecurityUtils.hasAnyRole(Roles.ADMIN)) {
                throw new AccessDeniedException("Only admins can create admin users");
            }
        }

        userRepository.save(user);
        return "User registered successfully!";
    }
}
