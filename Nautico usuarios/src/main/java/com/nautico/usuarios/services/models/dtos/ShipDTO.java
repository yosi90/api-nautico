package com.nautico.usuarios.services.models.dtos;

import com.nautico.usuarios.persistence.entities.ShipEntity;
import com.nautico.usuarios.services.models.Entities.DTOCosmicEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShipDTO extends DTOCosmicEntity {

    public ShipDTO(ShipEntity ship) {
        shipId = ship.getId();
        name = ship.getName();
        registration = ship.getRegistration();
        mooringNumber = ship.getMooringNumber();
        fee = ship.getFee();
        String ownerUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/member/{memberId}")
                .buildAndExpand(ship.getOwner().getId())
                .toUriString();
        newURI("Owner", ownerUri);
        String departuresUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/ship/{shipId}/departures")
                .buildAndExpand(shipId)
                .toUriString();
        newURI("Departures", departuresUri);
    }

    private Long shipId;
    private String name;
    private String registration;
    private Integer mooringNumber;
    private BigDecimal fee;
}