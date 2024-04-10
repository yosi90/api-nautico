package com.nautico.usuarios.controllers;

import com.nautico.usuarios.persistence.entities.DepartureEntity;
import com.nautico.usuarios.services.IDepartureService;
import com.nautico.usuarios.services.models.dtos.DepartureDTO;
import com.nautico.usuarios.services.models.dtos.ResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departure")
public class DepartureControllers {

    @Autowired
    IDepartureService departureService;

    @GetMapping("/{departureId}")
    public ResponseEntity<DepartureDTO> getDepartureById(@PathVariable Long departureId) {
        return departureService.getDepartureById(departureId);
    }

    @GetMapping
    public ResponseEntity<List<DepartureDTO>> getAllDepartures() {
        return departureService.getAllDepartures();
    }

    @PostMapping("/{shipId}/{userId}")
    public ResponseEntity<ResponseDTO> createDeparture(@PathVariable Long shipId, @PathVariable Long userId, @RequestBody @Valid DepartureEntity departureNew, BindingResult result) throws Exception {
        if (result != null && result.hasErrors()) {
            ResponseDTO response = new ResponseDTO();
            for (FieldError error : result.getFieldErrors())
                response.newError(String.format("%s: %s", error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        }
        return departureService.createDeparture(departureNew, shipId, userId);
    }

    @PutMapping("/{departureId}")
    public ResponseEntity<ResponseDTO> updateUser(@PathVariable Long departureId, @RequestBody DepartureEntity updatedDeparture) {
        return departureService.updateDeparture(departureId, updatedDeparture);
    }

    @DeleteMapping("/{departureId}")
    public ResponseEntity<ResponseDTO> removeUser(@PathVariable Long departureId) {
        return departureService.remove(departureId);
    }
}
