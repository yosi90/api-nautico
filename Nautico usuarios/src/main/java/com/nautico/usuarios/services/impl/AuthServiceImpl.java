package com.nautico.usuarios.services.impl;

import com.nautico.usuarios.persistence.entities.UserEntity;
import com.nautico.usuarios.persistence.repositories.UserRepository;
import com.nautico.usuarios.services.IAuthService;
import com.nautico.usuarios.services.IJWTUtilityService;
import com.nautico.usuarios.services.IUserService;
import com.nautico.usuarios.services.models.dtos.LoginDTO;
import com.nautico.usuarios.services.models.dtos.ResponseDTO;
import com.nautico.usuarios.services.models.dtos.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IUserService userService;

    @Autowired
    private IJWTUtilityService jwtUtilityService;

    @Override
    public ResponseEntity<UserDTO> login(LoginDTO login) throws Exception {
        try {
            Optional<UserEntity> userOPT = userRepository.findByEmail(login.getEmail());
            if (userOPT.isEmpty())
                return new ResponseEntity<>(new UserDTO(), HttpStatus.NOT_FOUND);
            else {
                UserEntity user = userOPT.get();
                if (verifyPassword(login.getPassword(), user.getPassword())) {
                    UserDTO userDTO = new UserDTO(user);
                    userDTO.setJWT(jwtUtilityService.generateJWT(user.getId()));
                    return new ResponseEntity<>(userDTO, HttpStatus.OK);
                } else
                    return new ResponseEntity<>(new UserDTO(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new UserDTO(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean verifyPassword(String enteredPassword, String storedPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(enteredPassword, storedPassword);
    }

    @Override
    public ResponseEntity<ResponseDTO> register(UserEntity userNew) {
        return userService.createUser(userNew);
    }
}
