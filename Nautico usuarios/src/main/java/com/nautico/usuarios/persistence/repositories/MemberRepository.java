package com.nautico.usuarios.persistence.repositories;

import com.nautico.usuarios.persistence.entities.MemberEntity;
import com.nautico.usuarios.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    Optional<MemberEntity> findByUser(UserEntity user);

    Optional<MemberEntity> findByUserId(Long userId);

    @Query("SELECT p.user FROM MemberEntity p WHERE p.user.id = :userId")
    Optional<UserEntity> findUserByUserId(Long userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM MemberEntity p WHERE p.id = :memberId")
    void deleteByMemberId(Long memberId);
}
