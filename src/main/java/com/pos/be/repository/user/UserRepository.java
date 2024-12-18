package com.pos.be.repository.user;

import com.pos.be.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    @Query("""
        SELECT u.username as username, u.firstName as firstName, u.lastName as lastName, u.address as address
         FROM User u WHERE u.username = ?1
    """)
    UserProfile getUserProfile(@Param("username") String username);

    interface UserProfile {
        String getUsername();
        String getFirstName();
        String getLastName();
        String getAddress();
    }
}
