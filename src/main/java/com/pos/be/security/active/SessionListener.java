package com.pos.be.security.active;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SessionListener implements HttpSessionListener {

    private final ActiveUserStore activeUserStore;

    public SessionListener(ActiveUserStore activeUserStore) {
        this.activeUserStore = activeUserStore;
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        activeUserStore.removeUser(username);
    }
}
