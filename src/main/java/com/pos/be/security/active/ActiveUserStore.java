package com.pos.be.security.active;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ActiveUserStore implements ApplicationListener<AuthenticationSuccessEvent> {

    private final Set<String> activeUsers = new HashSet<>();

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        String username = authentication.getName(); // Get the logged-in username

        synchronized (activeUsers) {
            activeUsers.add(username);
            System.out.println("User logged in: " + username);
        }
    }

    public Set<String> getActiveUsers() {
        return activeUsers;
    }

    public void removeUser(String username) {
        synchronized (activeUsers) {
            activeUsers.remove(username);
            System.out.println("User logged out: " + username);
        }
    }
}
