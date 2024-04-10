package com.nautico.usuarios.controllers;

import com.nautico.usuarios.persistence.entities.ShipEntity;
import com.nautico.usuarios.services.IShipService;
import com.nautico.usuarios.services.models.dtos.DepartureDTO;
import com.nautico.usuarios.services.models.dtos.ResponseDTO;
import com.nautico.usuarios.services.models.dtos.ShipDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ship")
public class ShipControllers {

    @Autowired
    IShipService shipService;


    @GetMapping("/{shipId}")
    public ResponseEntity<ShipDTO> getShipById(@PathVariable Long shipId) {
        return shipService.getShipById(shipId);
    }

    @GetMapping
    public ResponseEntity<List<ShipDTO>> getAllShips() {
        return shipService.getAllShips();
    }

    @GetMapping("/{shipId}/departures")
    public ResponseEntity<List<DepartureDTO>> getDepartures(@PathVariable Long shipId) {
        return shipService.getDepartures(shipId);
    }

    @PostMapping("/{memberId}")
    public ResponseEntity<ResponseDTO> createShip(@PathVariable Long memberId, @RequestBody @Valid ShipEntity shipNew, BindingResult result) throws Exception {
        if (result != null && result.hasErrors()) {
            ResponseDTO response = new ResponseDTO();
            for (FieldError error : result.getFieldErrors())
                response.newError(String.format("%s: %s", error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        }
        return shipService.createShip(shipNew, memberId);
    }

    @PutMapping("/{shipId}")
    public ResponseEntity<ResponseDTO> updateShip(@PathVariable Long shipId, @RequestBody ShipEntity updatedShip) {
        return shipService.updateShip(shipId, updatedShip);
    }

    @DeleteMapping("/{shipId}")
    public ResponseEntity<ResponseDTO> removeShip(@PathVariable Long shipId) {
        return shipService.removeShip(shipId);
    }
}