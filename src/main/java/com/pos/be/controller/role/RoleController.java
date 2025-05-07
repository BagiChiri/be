package com.pos.be.controller.role;

import com.pos.be.entity.user.Role;
import com.pos.be.exception.PermissionDeniedException;
import com.pos.be.security.rbac.Permissions;
import com.pos.be.security.rbac.SecurityUtils;
import com.pos.be.service.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAuthority('" + Permissions.READ_ROLE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public List<Role> list() {
        return roleService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> get(@PathVariable Integer id) {
        if (!SecurityUtils.hasPermission(Permissions.READ_ROLE) &&
                !SecurityUtils.hasPermission(Permissions.FULL_ACCESS)) {
            throw new PermissionDeniedException("You don't have permission to view roles");
        }
        return ResponseEntity.ok(roleService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('" + Permissions.CREATE_ROLE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public Role create(@RequestBody Role role) {
        return roleService.create(role);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.UPDATE_ROLE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public Role update(@PathVariable Integer id, @RequestBody Role role) {
        return roleService.update(id, role);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.DELETE_ROLE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}