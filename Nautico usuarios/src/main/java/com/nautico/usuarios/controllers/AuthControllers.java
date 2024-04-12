package com.nautico.usuarios.controllers;

import com.nautico.usuarios.persistence.entities.UserEntity;
import com.nautico.usuarios.services.IAuthService;
import com.nautico.usuarios.services.models.dtos.LoginDTO;
import com.nautico.usuarios.services.models.dtos.ResponseDTO;
import com.nautico.usuarios.services.models.dtos.UserDTO;
import com.nautico.usuarios.services.models.dtos.JwtTokenDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthControllers {

    @Autowired
    IAuthService authService;

    @PostMapping("/registerUser")
    private ResponseEntity<ResponseDTO> registerUser(@RequestBody @Valid UserEntity userNew, BindingResult result) throws Exception {
        if (result != null && result.hasErrors()) {
            ResponseDTO response = new ResponseDTO();
            for (FieldError error : result.getFieldErrors())
                response.newError(String.format("%s: %s", error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        }
        return authService.registerUser(userNew);
    }

    @PostMapping("/registerMember")
    private ResponseEntity<ResponseDTO> registerMember(@RequestBody @Valid UserEntity userNew, BindingResult result) throws Exception {
        if (result != null && result.hasErrors()) {
            ResponseDTO response = new ResponseDTO();
            for (FieldError error : result.getFieldErrors())
                response.newError(String.format("%s: %s", error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        }
        return authService.registerMember(userNew);
    }

    @PostMapping("/login")
    private ResponseEntity<UserDTO> login(@RequestBody LoginDTO loginRequest) throws Exception {
        return authService.login(loginRequest);
    }

    @PostMapping("/loginsimple")
    private ResponseEntity<JwtTokenDTO> loginSimple(@RequestBody LoginDTO loginRequest) throws Exception {
        return authService.loginSimple(loginRequest);
    }
}
