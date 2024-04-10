package com.nautico.usuarios.services.models.dtos;

import com.nautico.usuarios.persistence.entities.MemberEntity;
import com.nautico.usuarios.persistence.entities.ShipEntity;
import com.nautico.usuarios.services.models.Entities.DTOCosmicEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO extends DTOCosmicEntity {

    public MemberDTO(MemberEntity member) {
        memberId = member.getId();
        String userUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/user/{userId}")
                .buildAndExpand(member.getUser().getId())
                .toUriString();
        newURI("User", userUri);
        String shipsUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/member/{memberId}/ships")
                .buildAndExpand(member.getId())
                .toUriString();
        newURI("Ships", shipsUri);
    }

    private Long memberId;
}
