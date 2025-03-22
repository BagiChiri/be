package com.pos.be.service.user;

import com.pos.be.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserRepository.UserProfile profile(String username) {
        return userRepository.getUserProfile(username);
    }
}
