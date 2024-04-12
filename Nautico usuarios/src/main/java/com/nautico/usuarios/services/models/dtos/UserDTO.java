package com.nautico.usuarios.services.models.dtos;

import com.nautico.usuarios.persistence.entities.UserEntity;
import com.nautico.usuarios.services.models.Entities.DTOCosmicEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO extends DTOCosmicEntity {
    private Long userId;
    private String name;
    private String lastName;
    private String email;
    private Boolean isMember;

    public UserDTO(UserEntity user) {
        userId = user.getId();
        name = user.getName();
        lastName = user.getLastName();
        email = user.getEmail();
        isMember = user.getMember() != null;
        if (isMember) {
            String shipsUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/v1/member/{memberId}")
                    .buildAndExpand(user.getMember().getId())
                    .toUriString();
            newURI("ThisAsMember", shipsUri);
        }
        String departuresUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/user/{userId}/departures")
                .buildAndExpand(userId)
                .toUriString();
        newURI("DeparturesAsSkipper", departuresUri);
    }
}
