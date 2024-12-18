package com.pos.be.entity.user;

import com.pos.be.repository.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

//@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch the user from the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Convert User entity to UserDetails
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                // Map roles to their names
                .roles(user.getRoles().stream()
                        .map(Role::getName) // Extract the role name
                        .toArray(String[]::new))
                .accountExpired(false) // Set based on your entity logic
                .accountLocked(false)  // Set based on your entity logic
                .credentialsExpired(false) // Set based on your entity logic
                .disabled(!user.getEnabled()) // Set based on the `enabled` field
                .build();
    }
}
