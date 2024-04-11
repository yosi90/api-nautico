package com.nautico.usuarios.services.impl;

import com.nautico.usuarios.persistence.entities.DepartureEntity;
import com.nautico.usuarios.persistence.entities.MemberEntity;
import com.nautico.usuarios.persistence.entities.ShipEntity;
import com.nautico.usuarios.persistence.entities.UserEntity;
import com.nautico.usuarios.persistence.repositories.DepartureRepository;
import com.nautico.usuarios.persistence.repositories.ShipRepository;
import com.nautico.usuarios.persistence.repositories.UserRepository;
import com.nautico.usuarios.services.IDepartureService;
import com.nautico.usuarios.services.models.dtos.DepartureDTO;
import com.nautico.usuarios.services.models.dtos.ResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DepartureServiceImpl implements IDepartureService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShipRepository shipRepository;

    private final DepartureRepository departureRepository;

    @Autowired
    public DepartureServiceImpl(DepartureRepository departureRepository) {
        this.departureRepository = departureRepository;
    }

    @Override
    public ResponseEntity<DepartureDTO> getDepartureById(Long departureId) {
        try {
            DepartureEntity departure = departureRepository.findById(departureId).orElse(null);
            if (departure == null) return ResponseEntity.notFound().build();
            return new ResponseEntity<>(new DepartureDTO(departure), HttpStatus.FOUND);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<List<DepartureDTO>> getAllDepartures() {
        try {
            List<DepartureEntity> departures = departureRepository.findAll();
            List<DepartureDTO> departureDTOS = new ArrayList<>();
            if (departures.isEmpty()) return ResponseEntity.noContent().build();
            for (DepartureEntity departureEntity : departures)
                departureDTOS.add(departureEntity.ToDTO());
            return new ResponseEntity<>(departureDTOS, HttpStatus.FOUND);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> createDeparture(DepartureEntity departureNew, Long shipId, Long userId) {
        try {
            ResponseDTO response = new ResponseDTO();
            Optional<ShipEntity> ship = shipRepository.findById(shipId);
            if (ship.isEmpty())
                return ResponseEntity.notFound().build();
            List<DepartureEntity> conflictingDeparturesForShip = departureRepository.findConflictingDeparturesForShip(ship.get(),
                    departureNew.getDepartureDT(),
                    departureNew.getArrivalDT() == null ? departureNew.getDepartureDT() : departureNew.getArrivalDT());
            if(!conflictingDeparturesForShip.isEmpty()) {
                response.newError("Ese barco no está atracado ahora");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
            Optional<UserEntity> skipper = userRepository.findById(userId);
            if (skipper.isEmpty())
                return ResponseEntity.notFound().build();
            List<DepartureEntity> conflictingDeparturesForSkipper =
                    departureRepository.findConflictingDeparturesForSkipper(skipper.get(),
                            departureNew.getDepartureDT(),
                            departureNew.getArrivalDT() == null ? departureNew.getDepartureDT() : departureNew.getArrivalDT());
            if (!conflictingDeparturesForSkipper.isEmpty()) {
                response.newError("Esa persona ya está haciendo de patrón de otro barco");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
            if(departureNew.getArrivalDT() != null && departureNew.getArrivalDT().isBefore(departureNew.getDepartureDT())) {
                response.newError("La embarcación no puede llegar antes de salir");
                return new ResponseEntity<>(response, HttpStatus.FAILED_DEPENDENCY);
            }
            departureNew.setSkipper(skipper.get());
            departureNew.setShip(ship.get());
            departureRepository.save(departureNew);
            response.newMessage("Se ha creado la salida");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> updateDeparture(Long previousDepartureId, DepartureEntity updatedDeparture) {
        ResponseDTO response = new ResponseDTO();
        try {
            DepartureEntity previousDeparture = departureRepository.findById(previousDepartureId).orElseThrow(EntityNotFoundException::new);
            previousDeparture.setDepartureDT(updatedDeparture.getDepartureDT());
            previousDeparture.setArrivalDT(updatedDeparture.getArrivalDT());
            previousDeparture.setShip(updatedDeparture.getShip());
            previousDeparture.setSkipper(updatedDeparture.getSkipper());
            departureRepository.save(previousDeparture);
            response.newMessage("Salida actualizada");
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<ResponseDTO> remove(Long departureId) {
        ResponseDTO response = new ResponseDTO();
        try {
            Optional<DepartureEntity> departureEntity = departureRepository.findById(departureId);
            if (departureEntity.isPresent()) {
                DepartureEntity departure = departureEntity.get();
                departure.setShip(null);
                departure.setSkipper(null);
                departureRepository.save(departure);
                departureRepository.deleteByDepartureId(departure.getId());
                response.newMessage("Miembro eliminado");
                return ResponseEntity.ok(response);
            } else
                return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
