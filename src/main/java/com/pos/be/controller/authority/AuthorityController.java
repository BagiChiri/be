package com.pos.be.controller.authority;


import com.pos.be.entity.user.Authority;
import com.pos.be.exception.PermissionDeniedException;
import com.pos.be.security.rbac.Permissions;
import com.pos.be.security.rbac.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.pos.be.service.authority.AuthorityService;

import java.util.List;

@RestController
@RequestMapping("/api/authorities")
@RequiredArgsConstructor
public class AuthorityController {
    private final AuthorityService authService;

    @GetMapping
    @PreAuthorize("hasAuthority('" + Permissions.READ_PERMISSION + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public List<Authority> list() {
        return authService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Authority> get(@PathVariable Integer id) {
        if (!SecurityUtils.hasPermission(Permissions.READ_PERMISSION) &&
                !SecurityUtils.hasPermission(Permissions.FULL_ACCESS)) {
            throw new PermissionDeniedException("You don't have permission to view permissions");
        }
        return ResponseEntity.ok(authService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('" + Permissions.CREATE_PERMISSION + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public Authority create(@RequestBody Authority authority) {
        return authService.create(authority);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.UPDATE_PERMISSION + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public Authority update(@PathVariable Integer id, @RequestBody Authority authority) {
        return authService.update(id, authority);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.DELETE_PERMISSION + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        authService.delete(id);
        return ResponseEntity.noContent().build();
    }
}