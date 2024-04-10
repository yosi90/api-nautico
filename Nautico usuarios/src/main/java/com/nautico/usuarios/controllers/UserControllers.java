package com.nautico.usuarios.controllers;

import com.nautico.usuarios.services.IUserService;
import com.nautico.usuarios.persistence.entities.UserEntity;
import com.nautico.usuarios.services.models.dtos.DepartureDTO;
import com.nautico.usuarios.services.models.dtos.ResponseDTO;
import com.nautico.usuarios.services.models.dtos.UserDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/user")
public class UserControllers {

    @Autowired
    IUserService userService;


    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}/departures")
    public ResponseEntity<List<DepartureDTO>> getUserDepartures(@PathVariable Long userId) {
        return userService.getUserDepartures(userId);
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createUser(@RequestBody @Valid UserEntity userNew, BindingResult result) throws Exception {
        if (result != null && result.hasErrors()) {
            ResponseDTO response = new ResponseDTO();
            for (FieldError error : result.getFieldErrors())
                response.newError(String.format("%s: %s", error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        }
        return userService.createUser(userNew);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ResponseDTO> updateUser(@PathVariable Long userId, @RequestBody UserEntity updatedUser) {
        return userService.updateUser(userId, updatedUser);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ResponseDTO> updatePassword(@PathVariable Long userId, @RequestBody String passwordNew) {
        return userService.updatePassword(userId, passwordNew);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseDTO> removeUser(@PathVariable Long userId) {
        return userService.removeUser(userId);
    }
}
