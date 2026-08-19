package com.crm.backend.repository;

import com.crm.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET wrong_attempts = :attempts, is_locked = :isLocked WHERE id = :userId", nativeQuery = true)
    void updateAttemptsDirectly(@Param("userId") Long userId, @Param("attempts") int attempts, @Param("isLocked") boolean isLocked);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET wrong_attempts = 0, is_locked = false WHERE id = :userId", nativeQuery = true)
    void resetAttemptsDirectly(@Param("userId") Long userId);
}