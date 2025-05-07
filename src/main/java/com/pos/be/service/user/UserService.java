package com.pos.be.service.user;

import com.pos.be.entity.user.PasswordResetToken;
import com.pos.be.entity.user.User;
import com.pos.be.exception.PermissionDeniedException;
import com.pos.be.exception.ResourceNotFoundException;
import com.pos.be.repository.user.PasswordResetTokenRepository;
import com.pos.be.repository.user.UserRepository;
import com.pos.be.security.rbac.Permissions;
import com.pos.be.security.rbac.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserRepository.UserProfile profile(String username) {
        return userRepository.getUserProfile(username);
    }

    private final PasswordResetTokenRepository tokenRepo;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordResetTokenRepository tokenRepo, JavaMailSender mailSender, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepo = tokenRepo;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${app.resetTokenExpiryMinutes:15}")
    private int expiryMinutes;

    public List<User> findAll() {
        if (!SecurityUtils.hasPermission(Permissions.READ_USER)) {
            throw new PermissionDeniedException("You don't have permission to view users");
        }
        return userRepository.findAll();
    }

    public User findById(Integer id) {
        if (!SecurityUtils.hasPermission(Permissions.READ_USER)) {
            throw new PermissionDeniedException("You don't have permission to view users");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User: " + id));
    }

    public User create(User user) {
        if (!SecurityUtils.hasPermission(Permissions.CREATE_USER)) {
            throw new PermissionDeniedException("You don't have permission to create users");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User update(Integer id, User user) {
        if (!SecurityUtils.hasPermission(Permissions.UPDATE_USER)) {
            throw new PermissionDeniedException("You don't have permission to update users");
        }
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User: " + id));
        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        existing.setGender(user.getGender());
        existing.setAddress(user.getAddress());
        existing.setEnabled(user.isEnabled());
        // username/password left unchanged here
        return userRepository.save(existing);
    }

    public void delete(Integer id) {
        if (!SecurityUtils.hasPermission(Permissions.DELETE_USER)) {
            throw new PermissionDeniedException("You don't have permission to delete users");
        }
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User: " + id);
        }
        userRepository.deleteById(id);
    }

    public void lockUser(Integer id) {
        if (!SecurityUtils.hasPermission(Permissions.UPDATE_USER)) {
            throw new PermissionDeniedException("You don't have permission to lock users");
        }
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User: " + id));
        u.setEnabled(false);
        userRepository.save(u);
    }

    public void initiatePasswordReset(String email) {
        if (!userRepository.findByUsername(email).isPresent()) {
            throw new ResourceNotFoundException("User: " + email);
        }
        User user = userRepository.findByUsername(email).get();
        tokenRepo.deleteByUser_Id(user.getId());

        String token = UUID.randomUUID().toString();
        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken(token);
        prt.setUser(user);
        prt.setExpiry(Instant.now().plus(expiryMinutes, ChronoUnit.MINUTES));
        tokenRepo.save(prt);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(user.getUsername());
        msg.setSubject("Password reset code");
        msg.setText("Your reset code is: " + token);
        mailSender.send(msg);
    }

    public void confirmPasswordReset(String token, String newPassword) {
        PasswordResetToken prt = tokenRepo.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("PasswordResetToken: " + token));
        if (prt.getExpiry().isBefore(Instant.now())) {
            tokenRepo.delete(prt);
            throw new IllegalArgumentException("Token expired");
        }
        User user = prt.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepo.delete(prt);
    }
}
