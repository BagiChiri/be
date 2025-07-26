package com.pos.be.security.rbac;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return hasPrivilege(authentication, permission.toString());
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return hasPrivilege(authentication, permission.toString());
    }

    private boolean hasPrivilege(Authentication auth, String permission) {
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        if (auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Permissions.FULL_ACCESS))) {
            return true;
        }

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(p -> p.equals(permission));
    }
}