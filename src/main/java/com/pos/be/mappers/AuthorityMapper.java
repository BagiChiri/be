package com.pos.be.mappers;

import com.pos.be.dto.authority.AuthorityDTO;
import com.pos.be.entity.user.Authority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthorityMapper {

    public AuthorityDTO toDto(Authority authority) {
        if (authority == null) return null;

        AuthorityDTO dto = new AuthorityDTO();
        dto.setId(authority.getId());
        dto.setName(authority.getName());
        return dto;
    }

    public Authority toEntity(AuthorityDTO dto) {
        if (dto == null) return null;

        Authority authority = new Authority();
        authority.setId(dto.getId());
        authority.setName(dto.getName());
        return authority;
    }

    public List<AuthorityDTO> toDtoList(List<Authority> authorities) {
        return authorities == null ? null :
                authorities.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<Authority> toEntityList(List<AuthorityDTO> dtos) {
        return dtos == null ? null :
                dtos.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
