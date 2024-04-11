package com.nautico.usuarios.services;

import com.nautico.usuarios.persistence.entities.UserEntity;
import com.nautico.usuarios.services.models.dtos.JwtTokenDTO;
import com.nautico.usuarios.services.models.dtos.LoginDTO;
import com.nautico.usuarios.services.models.dtos.ResponseDTO;
import com.nautico.usuarios.services.models.dtos.UserDTO;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

public interface IAuthService {
    ResponseEntity<UserDTO> login(LoginDTO login) throws Exception;

    ResponseEntity<JwtTokenDTO> loginSimple(LoginDTO login) throws Exception;

    ResponseEntity<ResponseDTO> register(UserEntity user) throws Exception;
}
