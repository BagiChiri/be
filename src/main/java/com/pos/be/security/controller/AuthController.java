package com.pos.be.security.controller;

import com.pos.be.entity.User;
import com.pos.be.security.AuthenticationService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final AuthenticationManager authenticationManager;


    public void setJwtCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);   // Ensures the cookie cannot be accessed by JavaScript
        cookie.setSecure(true);     // Makes sure the cookie is only sent over HTTPS
        cookie.setPath("/");        // Cookie will be sent with every request
        cookie.setMaxAge(3600);     // 1 hour expiration time
//        cookie.setSameSite("None"); // Important for cross-origin requests
        response.addCookie(cookie);
    }
//    @PostMapping("/login")
//    public String login(HttpServletResponse response, @RequestBody LoginRequest loginRequest) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        loginRequest.getUsername(),
//                        loginRequest.getPassword()
//                )
//        );
//
//        String token = authenticationService.authenticate(authentication);
//        setJwtCookie(response, token);
//        return token;
//    }
@PostMapping("login")
public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
    try {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT
        String token = authenticationService.createToken(authentication);

        setJwtCookie(response, token);

        return ResponseEntity.ok().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}

    @PostMapping("/register")
    public String register(@RequestBody User user) throws RoleNotFoundException {
        return authenticationService.register(user);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/role")
    public String role() {
        printCurrentUserDetails();
        return "has Role";
    }

    public void printCurrentUserDetails() {
        // Retrieve the current authentication object
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
