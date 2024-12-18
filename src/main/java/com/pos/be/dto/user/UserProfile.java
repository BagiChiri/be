package com.pos.be.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserProfile {
    private String firstName;
    private String lastName;
    private String username;
    private String address;
}
