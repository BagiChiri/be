package com.pos.be.repository.user;

import com.pos.be.entity.user.Authority;
import com.pos.be.security.rbac.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
    Optional<Authority> findByName(String authorityName);
    boolean existsByName(String name);
}

