package com.nautico.usuarios.services;

import com.nautico.usuarios.persistence.entities.UserEntity;
import com.nautico.usuarios.services.models.dtos.DepartureDTO;
import com.nautico.usuarios.services.models.dtos.ResponseDTO;
import com.nautico.usuarios.services.models.dtos.UserDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IUserService {

    ResponseEntity<UserDTO> getUserById(Long userId);

    ResponseEntity<List<UserDTO>> getAllUsers();

    ResponseEntity<List<DepartureDTO>> getUserDepartures(Long userId);

    ResponseEntity<ResponseDTO> createUser(UserEntity userNew);

    ResponseEntity<ResponseDTO> updateUser(Long id, UserEntity updatedUser);

    ResponseEntity<ResponseDTO> updatePassword(Long id, String newPassword);

    ResponseEntity<ResponseDTO> removeUser(Long userId);
}
