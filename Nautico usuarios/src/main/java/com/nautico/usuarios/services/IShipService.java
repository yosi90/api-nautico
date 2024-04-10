package com.nautico.usuarios.services;

import com.nautico.usuarios.persistence.entities.ShipEntity;
import com.nautico.usuarios.services.models.dtos.DepartureDTO;
import com.nautico.usuarios.services.models.dtos.ResponseDTO;
import com.nautico.usuarios.services.models.dtos.ShipDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IShipService {

    ResponseEntity<ShipDTO> getShipById(Long userId);

    ResponseEntity<List<ShipDTO>> getAllShips();

    ResponseEntity<List<DepartureDTO>> getDepartures(Long shipId);

    ResponseEntity<ResponseDTO> createShip(ShipEntity shipNew, Long memberId);

    ResponseEntity<ResponseDTO> updateShip(Long previousShipId, ShipEntity updatedShip);

    ResponseEntity<ResponseDTO> removeShip(Long shipId);
}
