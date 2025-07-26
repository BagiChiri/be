package com.pos.be.mappers;

import com.pos.be.dto.user.UserDTO;
import com.pos.be.entity.user.Role;
import com.pos.be.entity.user.User;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setGender(user.getGender());
        dto.setAddress(user.getAddress());
        dto.setUsername(user.getUsername());
        dto.setEnabled(user.getEnabled());

        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles()
                    .stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet()));
        }

        if (user.getAuthorities() != null) {
            dto.setAuthorities(
                    user.getAuthorities().stream()
                            .map(a -> a.getAuthority())
                            .collect(Collectors.toSet())
            );

        }

        return dto;
    }

    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setId(dto.getId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setGender(dto.getGender());
        user.setAddress(dto.getAddress());
        user.setUsername(dto.getUsername());
        user.setEnabled(dto.getEnabled());

        user.setRoles(new HashSet<>());
        user.setAuthorities(new HashSet<>());

        return user;
    }
}
