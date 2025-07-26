package com.pos.be.service.role;

import com.pos.be.entity.user.Role;
import com.pos.be.exception.PermissionDeniedException;
import com.pos.be.exception.ResourceNotFoundException;
import com.pos.be.repository.user.RoleRepository;
import com.pos.be.security.rbac.Permissions;
import com.pos.be.security.rbac.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {
    private final RoleRepository roleRepository;

    public List<Role> findAll() {
        if (!SecurityUtils.hasPermission(Permissions.READ_ROLE)) {
            throw new PermissionDeniedException("You don't have permission to view roles");
        }
        return roleRepository.findAll();
    }

    public Role findById(Integer id) {
        if (!SecurityUtils.hasPermission(Permissions.READ_ROLE)) {
            throw new PermissionDeniedException("You don't have permission to view roles");
        }
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role: " + id));
    }

    public Role create(Role role) {
        if (!SecurityUtils.hasPermission(Permissions.CREATE_ROLE)) {
            throw new PermissionDeniedException("You don't have permission to create roles");
        }
        if (roleRepository.existsByName(role.getName())) {
            throw new IllegalArgumentException("Role name already in use");
        }
        return roleRepository.save(role);
    }

    public Role update(Integer id, Role role) {
        if (!SecurityUtils.hasPermission(Permissions.UPDATE_ROLE)) {
            throw new PermissionDeniedException("You don't have permission to update roles");
        }
        Role existing = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role: " + id));
        existing.setName(role.getName());
        return roleRepository.save(existing);
    }

    public void delete(Integer id) {
        if (!SecurityUtils.hasPermission(Permissions.DELETE_ROLE)) {
            throw new PermissionDeniedException("You don't have permission to delete roles");
        }
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role: " + id);
        }
        roleRepository.deleteById(id);
    }
}
