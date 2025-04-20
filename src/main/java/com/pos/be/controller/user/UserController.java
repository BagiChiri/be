package com.pos.be.controller.user;

import com.pos.be.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> profile(
            @Param("username") String username) {
        return ResponseEntity.ok().body(
                userService.profile(username)
        );
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> response = new HashMap<>();
        response.put("username", jwt.getClaimAsString("preferred_username"));

        List<String> roles = jwt.getClaimAsStringList("roles");
        List<String> permissions = jwt.getClaimAsStringList("permissions");

        response.put("roles", roles);
        response.put("permissions", permissions);

        return ResponseEntity.ok(response);
    }

}


