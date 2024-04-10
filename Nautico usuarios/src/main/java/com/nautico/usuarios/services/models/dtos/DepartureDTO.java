package com.nautico.usuarios.services.models.dtos;

import com.nautico.usuarios.persistence.entities.DepartureEntity;
import com.nautico.usuarios.services.models.Entities.DTOCosmicEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DepartureDTO extends DTOCosmicEntity {

    public DepartureDTO(DepartureEntity departure) {
        departureId = departure.getId();
        departureDT = departure.getDepartureDT();
        arrivalDT = departure.getArrivalDT();
        String shipUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/ship/{shipId}")
                .buildAndExpand(departure.getShip().getId())
                .toUriString();
        newURI("Ship", shipUri);
        String skipperUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/user/{userId}")
                .buildAndExpand(departure.getSkipper().getId())
                .toUriString();
        newURI("Skipper", skipperUri);
    }

    private Long departureId;
    private LocalDateTime departureDT;
    private LocalDateTime arrivalDT;
}
