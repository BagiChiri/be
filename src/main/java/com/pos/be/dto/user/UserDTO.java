package com.pos.be.dto.user;

import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String gender;
    private String address;
    private String username;
    private Boolean enabled;
    private Set<String> roles;
    private Set<String> authorities;
}