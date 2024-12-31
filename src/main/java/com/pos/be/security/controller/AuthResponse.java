package com.pos.be.security.controller;

import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class AuthResponse {
    private String username;
    private String token;
}
