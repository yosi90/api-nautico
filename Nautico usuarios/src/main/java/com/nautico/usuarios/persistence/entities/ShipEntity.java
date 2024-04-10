package com.nautico.usuarios.persistence.entities;

import com.nautico.usuarios.services.models.dtos.DepartureDTO;
import com.nautico.usuarios.services.models.dtos.ShipDTO;
import com.nautico.usuarios.services.models.dtos.UserDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ships")
public class ShipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    LocalDateTime lifeSpan = LocalDateTime.now();

    @NotBlank
    @Pattern(regexp = "^[A-Z]{2}-\\d{4}-[A-Z]{2}$") //Ejemplos de matrícula correcta: AB-1234-CD” o “XY-5678-ZZ
    private String registration;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z]{3,15}")
    private String name;

    @NotNull
    private Integer mooringNumber;

    @NotNull
    private BigDecimal fee;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private MemberEntity owner;

    @OneToMany(mappedBy = "ship", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DepartureEntity> departures;

    public List<DepartureDTO> getDeparturesDTOs() {
        List<DepartureDTO> departureDTOS = new ArrayList<>();
        for (DepartureEntity departure : departures)
            departureDTOS.add(departure.ToDTO());
        return departureDTOS;
    }

    public ShipDTO ToDTO(){
        return new ShipDTO(this);
    }
}
