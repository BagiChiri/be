package com.pos.be.service.user;

//import com.pos.be.dto.user.UserProfile;
import com.pos.be.dto.user.UserProfile;
import com.pos.be.entity.user.User;
import com.pos.be.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserRepository.UserProfile profile(String username) {
        return userRepository.getUserProfile(username);
    }
}
