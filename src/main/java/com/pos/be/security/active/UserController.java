package com.pos.be.security.active;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

//@RestController
public class UserController {

    private final ActiveUserStore activeUserStore;

    public UserController(ActiveUserStore activeUserStore) {
        this.activeUserStore = activeUserStore;
    }

    @GetMapping("/active-users")
    public Set<String> getActiveUsers() {
        return activeUserStore.getActiveUsers();
    }
}
