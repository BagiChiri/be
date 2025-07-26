package com.pos.be.controller.user;

import com.pos.be.entity.user.User;
import com.pos.be.exception.PermissionDeniedException;
import com.pos.be.security.rbac.Permissions;
import com.pos.be.security.rbac.SecurityUtils;
import com.pos.be.service.user.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    @PreAuthorize("hasAuthority('" + Permissions.READ_USER + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public List<User> list() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable Integer id) {
        if (!SecurityUtils.hasPermission(Permissions.READ_USER) &&
                !SecurityUtils.hasPermission(Permissions.FULL_ACCESS)) {
            throw new PermissionDeniedException("You don't have permission to view users");
        }
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('" + Permissions.CREATE_USER + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<User> create(@RequestBody User user) {
        User created = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.UPDATE_USER + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public User update(@PathVariable Integer id, @RequestBody User user) {
        return userService.update(id, user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.DELETE_USER + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/lock")
    @PreAuthorize("hasAuthority('" + Permissions.UPDATE_USER + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<Void> lock(@PathVariable Integer id) {
        userService.lockUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password-reset-request")
    public ResponseEntity<Void> requestReset(@RequestBody ResetRequest r) {
        userService.initiatePasswordReset(r.getEmail());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/password-reset-confirm")
    public ResponseEntity<Void> confirmReset(@RequestBody ResetConfirm c) {
        userService.confirmPasswordReset(c.getToken(), c.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @Data
    static class ResetRequest {
        private String email;
    }

    @Data
    static class ResetConfirm {
        private String token, newPassword;
    }

}


