package com.nautico.usuarios.services.impl;

import com.nautico.usuarios.persistence.entities.DepartureEntity;
import com.nautico.usuarios.persistence.entities.MemberEntity;
import com.nautico.usuarios.persistence.entities.ShipEntity;
import com.nautico.usuarios.persistence.entities.UserEntity;
import com.nautico.usuarios.persistence.repositories.UserRepository;
import com.nautico.usuarios.services.IUserService;
import com.nautico.usuarios.services.models.dtos.DepartureDTO;
import com.nautico.usuarios.services.models.dtos.ResponseDTO;
import com.nautico.usuarios.services.models.dtos.ShipDTO;
import com.nautico.usuarios.services.models.dtos.UserDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public ResponseEntity<UserDTO> getUserById(Long userId) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) return new ResponseEntity<>(new UserDTO(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(new UserDTO(user), HttpStatus.FOUND);
    }

    @Override
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        List<UserDTO> userDTOS = new ArrayList<>();
        if (users.isEmpty()) return new ResponseEntity<>(userDTOS, HttpStatus.NO_CONTENT);
        for (UserEntity userEntity : users)
            userDTOS.add(userEntity.ToDTO());
        return new ResponseEntity<>(userDTOS, HttpStatus.FOUND);
    }

    @Override
    public ResponseEntity<List<DepartureDTO>> getUserDepartures(Long userId) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null)
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        if (user.getDepartures().isEmpty())
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(user.getDeparturesDTOs(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseDTO> createUser(UserEntity userNew) {
        try {
            ResponseDTO response = new ResponseDTO();
            Optional<UserEntity> existingUser = userRepository.findByEmail(userNew.getEmail());
            if (existingUser.isPresent()) {
                response.newError("Email ya registrado");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
            UserEntity user = findUpdatableUser(); //Forzamos la indempotencia
            ResponseEntity<ResponseDTO> responseEntity = updateUser(user.getId(), userNew);
            return responseEntity;
        } catch (Exception e) {
            ResponseDTO response = new ResponseDTO();
            response.newError(e.toString());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private UserEntity findUpdatableUser() {
        int threshold = 3600;
        Optional<UserEntity> userUpdatable = userRepository.findFirstUpdatable(threshold);
        if (userUpdatable.isEmpty()) {
            UserEntity userEntity = new UserEntity();
            userEntity.setName("NombreNoValido");
            userEntity.setLastName("ApellidoNoValido");
            userEntity.setEmail("emailn@oval.ido");
            userEntity.setPassword("ContraseñaNoValida");
            userEntity = userRepository.save(userEntity);
            return userEntity;
        } else return userUpdatable.get();
    }

    @Override
    public ResponseEntity<ResponseDTO> updateUser(Long id, UserEntity updatedUser) {
        ResponseDTO response = new ResponseDTO();
        try {
            UserEntity previousUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
            previousUser.setLifeSpan(updatedUser.getLifeSpan().plusYears(100));
            String OGName = previousUser.getName();
            previousUser.setName(updatedUser.getName());
            previousUser.setLastName(updatedUser.getLastName());
            previousUser.setEmail(updatedUser.getEmail());
            if (!updatedUser.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#ñÑ])[A-Za-z\\d@$!%*?&#ñÑ]{8,}$")) {
                response.newError("Contraseña no válida");
                return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
            }
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
            previousUser.setPassword(encoder.encode(updatedUser.getPassword()));
            userRepository.save(previousUser);
            response.newMessage(!Objects.equals(OGName, "NombreNoValido") ? "Usuario creado" : "Usuario actualizado");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            response.newError("Id no encontrada");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.newError(e.toString());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> updatePassword(Long id, String passwordNew) {
        ResponseDTO response = new ResponseDTO();
        try {
            UserEntity previousUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
            if (!passwordNew.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#ñÑ])[A-Za-z\\d@$!%*?&#ñÑ]{8,}$")) {
                response.newError("Contraseña no válida");
                return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
            }
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
            previousUser.setPassword(encoder.encode(passwordNew));
            userRepository.save(previousUser);
            response.newMessage("Contraseña actualizada");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            response.newError("Id no encontrada");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.newError(e.toString());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> removeUser(Long userId) {
        ResponseDTO response = new ResponseDTO();
        try {
            userRepository.deleteById(userId);
            response.newMessage("Usuario borrado");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.newError(e.toString());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}