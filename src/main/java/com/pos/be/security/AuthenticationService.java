package com.pos.be.security;

import com.pos.be.entity.*;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    private UserRepository userRepository;
    private ApplicationContext applicationContext;

    private RoleRepository roleRepository;

    public AuthenticationService(ApplicationContext applicationContext, UserRepository userRepository, RoleRepository roleRepository) {
        this.applicationContext = applicationContext;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    public String authenticate(Authentication authentication) {
        return createToken(authentication);
    }

    public String register(User user) throws RoleNotFoundException {
        // Check if the username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists!");
        }

        // Encode the user's password
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);

        // Assign roles (querying from database)
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role defaultRole = roleRepository.findById(1)
                    .orElseThrow(() -> new RoleNotFoundException("Default role not found"));// TODO: 11/10/2024 remove default role
            user.setRoles(new HashSet<>(Collections.singletonList(defaultRole)));
        }

        // Save the user to the database
        userRepository.save(user);
        return "User registered successfully!";
    }


    public String createToken(Authentication authentication) {
        var claims = JwtClaimsSet.builder()
                .issuer("http://localhost:8080/pos")
                .issuedAt(Instant.now())
                .subject(authentication.getName())
                .expiresAt(Instant.now().plusSeconds(15 * 60))
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
