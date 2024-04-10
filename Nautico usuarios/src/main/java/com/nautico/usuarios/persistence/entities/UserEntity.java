package com.nautico.usuarios.persistence.entities;

import com.nautico.usuarios.services.models.dtos.DepartureDTO;
import com.nautico.usuarios.services.models.dtos.ShipDTO;
import com.nautico.usuarios.services.models.dtos.UserDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    LocalDateTime lifeSpan = LocalDateTime.now();

    @NotBlank
    @Size(min = 3, max = 15)
    @Pattern(regexp = "^[a-zA-Z]{3,15}")
    private String name;

    @NotBlank
    @Size(min = 3, max = 30)
    @Pattern(regexp = "^[a-zA-Z]{3,30}")
    private String lastName;

    @Pattern(regexp = "[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
            + "[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
            + "(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9]"
            + "(?:[a-zA-Z0-9-]*[a-zA-Z0-9])?", message = "Email no válido")
    private String email;

    /*@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#ñÑ])[A-Za-z\\d@$!%*?&#ñÑ]{8,}$",
    message = "La contraseña debe contener al menos 1 mayúscula, 1 minúscula, 1 número, 1 símbolo y tener al menos 8 caracteres")*/
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MemberEntity member;

    @OneToMany(mappedBy = "skipper", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DepartureEntity> departures;

    public List<DepartureDTO> getDeparturesDTOs() {
        List<DepartureDTO> departureDTOs = new ArrayList<>();
        for (DepartureEntity departure : departures)
            departureDTOs.add(departure.ToDTO());
        return departureDTOs;
    }

    public UserDTO ToDTO(){
        return new UserDTO(this);
    }
}
