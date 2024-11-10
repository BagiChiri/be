package com.pos.be.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    @GetMapping("/welcome")
    public Response welcome() {
        return new Response("G aayan nu");
    }
}

@AllArgsConstructor
@Setter
@Getter
class Response {
    private String message;
}
