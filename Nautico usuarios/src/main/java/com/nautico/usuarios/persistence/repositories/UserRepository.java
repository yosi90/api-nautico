package com.nautico.usuarios.persistence.repositories;

import com.nautico.usuarios.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE TIMESTAMPDIFF(SECOND, u.lifeSpan, CURRENT_TIMESTAMP) >= :threshold")
    Optional<UserEntity> findFirstUpdatable(int threshold);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u SET u.password = :newPassword WHERE u.id = :id")
    void updatePasswordByHash(@Param("id") Long id, @Param("newPassword") String newPassword);
}
