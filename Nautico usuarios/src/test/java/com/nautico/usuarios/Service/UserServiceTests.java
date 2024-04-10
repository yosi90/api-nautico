package com.nautico.usuarios.Service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.nautico.usuarios.persistence.entities.UserEntity;
import com.nautico.usuarios.persistence.repositories.UserRepository;
import com.nautico.usuarios.services.impl.UserServiceImpl;
import com.nautico.usuarios.services.models.dtos.ResponseDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity user;

    @Transactional
    @BeforeEach
    void setup() {
        user = UserEntity.builder()
                .name("Nombre")
                .lastName("Apellido")
                .email("ant@onio.es")
                .password("Cont@1234")
                .build();
    }

    @Test
    @DisplayName("Guardar un usuario")
    @Order(3)
    @MockitoSettings(strictness = Strictness.LENIENT)
    void saveUser() {
        given(userRepository.findByEmail(user.getEmail()))
                .willReturn(Optional.empty());
        given(userRepository.save(any(UserEntity.class))).will(invocation -> {
            UserEntity savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        ResponseEntity<ResponseDTO> response = userService.createUser(user);
        System.out.println(response.getBody().getMessages());
        assertThat(response.getBody().getNumberOfErrors()).isEqualTo(0);
    }
}
