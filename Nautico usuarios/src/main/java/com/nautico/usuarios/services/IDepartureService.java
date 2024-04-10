package com.nautico.usuarios.services;

import com.nautico.usuarios.persistence.entities.DepartureEntity;
import com.nautico.usuarios.services.models.dtos.DepartureDTO;
import com.nautico.usuarios.services.models.dtos.ResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IDepartureService {

    ResponseEntity<DepartureDTO> getDepartureById(Long departureId);

    ResponseEntity<List<DepartureDTO>> getAllDepartures();

    ResponseEntity<ResponseDTO> createDeparture(DepartureEntity departureNew, Long userId, Long shipId);

    ResponseEntity<ResponseDTO> updateDeparture(Long previousDepartureId, DepartureEntity updatedDeparture);

    ResponseEntity<ResponseDTO> remove(Long departureId);
}
