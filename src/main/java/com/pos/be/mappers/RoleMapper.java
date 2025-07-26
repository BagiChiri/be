package com.pos.be.mappers;

import com.pos.be.dto.role.RoleDTO;
import com.pos.be.entity.user.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO toDto(Role role);

    Role toEntity(RoleDTO dto);
}
