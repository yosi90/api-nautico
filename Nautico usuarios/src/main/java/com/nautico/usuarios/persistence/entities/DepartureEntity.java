package com.nautico.usuarios.persistence.entities;

import com.nautico.usuarios.services.models.dtos.DepartureDTO;
import com.nautico.usuarios.services.models.dtos.UserDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "departures")
public class DepartureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDateTime departureDT;

    private LocalDateTime arrivalDT;

    @ManyToOne
    @JoinColumn(name = "ship_id")
    private ShipEntity ship;

    @ManyToOne
    @JoinColumn(name = "skipper_id")
    private UserEntity skipper;

    public DepartureDTO ToDTO(){
        return new DepartureDTO(this);
    }
}
