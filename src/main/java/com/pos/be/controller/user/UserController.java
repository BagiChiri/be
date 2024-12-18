package com.pos.be.controller.user;

//import com.pos.be.dto.user.UserProfile;
import com.pos.be.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> profile(
            @Param("username") String username) {
        return ResponseEntity.ok().body(
                userService.profile(username)
        );
    }
}


