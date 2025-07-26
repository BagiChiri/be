package com.pos.be.service.authority;

import com.pos.be.entity.user.Authority;
import com.pos.be.exception.PermissionDeniedException;
import com.pos.be.exception.ResourceNotFoundException;
import com.pos.be.repository.user.AuthorityRepository;
import com.pos.be.security.rbac.Permissions;
import com.pos.be.security.rbac.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthorityService {
    private final AuthorityRepository authorityRepository;

    public List<Authority> findAll() {
        if (!SecurityUtils.hasPermission(Permissions.READ_PERMISSION)) {
            throw new PermissionDeniedException("You don't have permission to view permissions");
        }
        return authorityRepository.findAll();
    }

    public Authority findById(Integer id) {
        if (!SecurityUtils.hasPermission(Permissions.READ_PERMISSION)) {
            throw new PermissionDeniedException("You don't have permission to view permissions");
        }
        return authorityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Authority: " + id));
    }

    public Authority create(Authority authority) {
        if (!SecurityUtils.hasPermission(Permissions.CREATE_PERMISSION)) {
            throw new PermissionDeniedException("You don't have permission to create permissions");
        }
        if (authorityRepository.existsByName(authority.getName())) {
            throw new IllegalArgumentException("Permission name already in use");
        }
        return authorityRepository.save(authority);
    }

    public Authority update(Integer id, Authority authority) {
        if (!SecurityUtils.hasPermission(Permissions.UPDATE_PERMISSION)) {
            throw new PermissionDeniedException("You don't have permission to update permissions");
        }
        Authority existing = authorityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Authority: " + id));
        existing.setName(authority.getName());
        return authorityRepository.save(existing);
    }

    public void delete(Integer id) {
        if (!SecurityUtils.hasPermission(Permissions.DELETE_PERMISSION)) {
            throw new PermissionDeniedException("You don't have permission to delete permissions");
        }
        if (!authorityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Authority: " + id);
        }
        authorityRepository.deleteById(id);
    }
}
