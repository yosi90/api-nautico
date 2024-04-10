package com.nautico.usuarios.persistence.entities;

import com.nautico.usuarios.services.models.dtos.MemberDTO;
import com.nautico.usuarios.services.models.dtos.ShipDTO;
import com.nautico.usuarios.services.models.dtos.UserDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "members")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShipEntity> ships;

    public List<ShipDTO> getShipDTOs() {
        List<ShipDTO> shipDTOs = new ArrayList<>();
        for (ShipEntity ship : ships)
            shipDTOs.add(ship.ToDTO());
        return shipDTOs;
    }

    public MemberDTO ToDTO() {
        return new MemberDTO(this);
    }
}
