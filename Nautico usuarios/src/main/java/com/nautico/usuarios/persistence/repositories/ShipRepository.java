package com.nautico.usuarios.persistence.repositories;

import com.nautico.usuarios.persistence.entities.ShipEntity;
import com.nautico.usuarios.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ShipRepository extends JpaRepository<ShipEntity, Long> {

    Optional<ShipEntity> findByRegistration(String registration);

    @Query("SELECT u FROM ShipEntity u WHERE TIMESTAMPDIFF(SECOND, u.lifeSpan, CURRENT_TIMESTAMP) >= :threshold")
    Optional<ShipEntity> findFirstUpdatable(int threshold);

    @Transactional
    @Modifying
    @Query("DELETE FROM ShipEntity p WHERE p.id = :shipId")
    void deleteByShipId(Long shipId);
}
