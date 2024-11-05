package com.pos.be.security.controller;

import com.pos.be.entity.User;
import com.pos.be.security.AuthenticationService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;

@RestController
public class AuthController {

    private AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public String login(Authentication authentication) {
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
