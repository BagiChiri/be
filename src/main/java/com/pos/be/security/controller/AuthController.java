package com.pos.be.security.controller;

import com.pos.be.entity.User;
import com.pos.be.security.AuthenticationService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        return authenticationService.authenticate(authentication);
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) throws RoleNotFoundException {
        return authenticationService.register(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/role")
    public String role() {
        return "has Role";
    }
}
