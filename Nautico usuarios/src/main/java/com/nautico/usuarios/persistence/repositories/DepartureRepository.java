package com.nautico.usuarios.persistence.repositories;

import com.nautico.usuarios.persistence.entities.DepartureEntity;
import com.nautico.usuarios.persistence.entities.ShipEntity;
import com.nautico.usuarios.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DepartureRepository extends JpaRepository<DepartureEntity, Long> {

    Optional<DepartureEntity> findBySkipper(UserEntity user);

    Optional<DepartureEntity> findByShip(ShipEntity ship);

    @Query("SELECT d FROM DepartureEntity d WHERE d.skipper = :skipper " +
            "AND (" +
            "   (:newDepartureTime BETWEEN d.departureDT AND d.arrivalDT) OR " +
            "   (:newArrivalTime BETWEEN d.departureDT AND d.arrivalDT) OR " +
            "   (d.departureDT BETWEEN :newDepartureTime AND :newArrivalTime) OR " +
            "   (d.arrivalDT BETWEEN :newDepartureTime AND :newArrivalTime))")
    List<DepartureEntity> findConflictingDeparturesForSkipper(
            @Param("skipper") UserEntity skipper,
            @Param("newDepartureTime") LocalDateTime newDepartureTime,
            @Param("newArrivalTime") LocalDateTime newArrivalTime
    );

    @Query("SELECT d FROM DepartureEntity d WHERE d.ship = :ship " +
            "AND (" +
            "   (:newDepartureTime BETWEEN d.departureDT AND d.arrivalDT) OR " +
            "   (:newArrivalTime BETWEEN d.departureDT AND d.arrivalDT) OR " +
            "   (d.departureDT BETWEEN :newDepartureTime AND :newArrivalTime) OR " +
            "   (d.arrivalDT BETWEEN :newDepartureTime AND :newArrivalTime))")
    List<DepartureEntity> findConflictingDeparturesForShip(
            @Param("ship") ShipEntity ship,
            @Param("newDepartureTime") LocalDateTime newDepartureTime,
            @Param("newArrivalTime") LocalDateTime newArrivalTime
    );

    @Transactional
    @Modifying
    @Query("DELETE FROM DepartureEntity p WHERE p.id = :departureId")
    void deleteByDepartureId(Long departureId);
}
