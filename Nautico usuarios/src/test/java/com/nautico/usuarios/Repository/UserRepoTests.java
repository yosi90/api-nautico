package com.nautico.usuarios.Repository;

import com.nautico.usuarios.persistence.entities.UserEntity;
import com.nautico.usuarios.persistence.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test") //Esta línea decide que perfil de applicación tomar en cuenta para el test.
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) Esto lanza las pruebas a la base de datos en producción a la que aputan los test, en lugar de una base de datos embebida.
public class UserRepoTests {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @BeforeEach
    void setup() {
        var res = userRepository.findAll().size();
        if(res > 0)
            return;
        String[] letras = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        for (int i = 0; i < 10; i++) {
            UserEntity user = UserEntity.builder()
                    .name(String.format("Nombre%s", letras[i]))
                    .lastName(String.format("Apellido%s", letras[i]))
                    .email(String.format("ant%s@onio.es", letras[i]))
                    .password("Cont@1234")
                    .build();
            userRepository.save(user);
            System.out.println(user);
        }
    }

    @Test
    @DisplayName("Recibir colección de usuarios")
    @Transactional
    @Order(1)
    void userCollection() {
        var res = userRepository.findAll();
        assertThat(res).isNotNull();
        assertThat(res).isNotEmpty();
        assertThat(res.size()).isEqualTo(11);
    }

    @RepeatedTest(3)
    @DisplayName("Recibir usuario por id")
    @Transactional
    @Order(2)
    void userById() {
        Random rnd = new Random();
        Optional<UserEntity> res = userRepository.findById((long)(rnd.nextInt(9) + 1));
        assertThat(res).isNotNull();
    }

    @Test
    @DisplayName("Guardar nuevo usuario")
    @Transactional
    @Commit
    @Order(3)
    void saveUser() {
        UserEntity user = UserEntity.builder()
                .name("Nombre")
                .lastName("Apellido")
                .email("ant@onio.es")
                .password("Cont@1234")
                .build();
        UserEntity res = userRepository.save(user);
        assertThat(res).isNotNull();
        assertThat(res.getId()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Actualizar un usuario")
    @Transactional
    @Commit
    @Order(4)
    void updateUser() {
        Optional<UserEntity> user = userRepository.findById(1L);
        assertThat(user).isPresent();
        UserEntity userToUpdate = user.get();
        userToUpdate.setName("Actualizado");
        UserEntity updatedUser = userRepository.save(userToUpdate);
        assertThat(updatedUser.getName()).isEqualTo("Actualizado");
    }

    @Test
    @DisplayName("Eliminar un usuario")
    @Transactional
    @Commit
    @Order(5)
    void removeUser() {
        userRepository.deleteById(1L);
        Optional<UserEntity> userVoid = userRepository.findById(1L);
        assertThat(userVoid).isEmpty();
    }
}
