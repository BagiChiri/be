// src/main/java/com/pos/be/repository/PasswordResetTokenRepository.java
package com.pos.be.repository.user;

import com.pos.be.entity.user.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser_Id(Integer userId);
}
