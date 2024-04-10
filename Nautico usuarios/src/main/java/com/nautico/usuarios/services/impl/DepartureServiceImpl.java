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
        DepartureEntity departure = departureRepository.findById(departureId).orElse(null);
        if (departure == null) return new ResponseEntity<>(new DepartureDTO(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(new DepartureDTO(departure), HttpStatus.FOUND);
    }

    @Override
    public ResponseEntity<List<DepartureDTO>> getAllDepartures() {
        List<DepartureEntity> departures = departureRepository.findAll();
        List<DepartureDTO> departureDTOS = new ArrayList<>();
        if (departures.isEmpty()) return new ResponseEntity<>(departureDTOS, HttpStatus.NO_CONTENT);
        for (DepartureEntity departureEntity : departures)
            departureDTOS.add(departureEntity.ToDTO());
        return new ResponseEntity<>(departureDTOS, HttpStatus.FOUND);
    }

    @Override
    public ResponseEntity<ResponseDTO> createDeparture(DepartureEntity departureNew, Long shipId, Long userId) {
        try {
            ResponseDTO response = new ResponseDTO();
            Optional<ShipEntity> ship = shipRepository.findById(shipId);
            if (ship.isEmpty()) {
                response.newError("Barco no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            List<DepartureEntity> conflictingDeparturesForShip = departureRepository.findConflictingDeparturesForShip(ship.get(),
                    departureNew.getDepartureDT(),
                    departureNew.getArrivalDT() == null ? departureNew.getDepartureDT() : departureNew.getArrivalDT());
            if(!conflictingDeparturesForShip.isEmpty()) {
                response.newError("Ese barco no está atracado ahora");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
            Optional<UserEntity> skipper = userRepository.findById(userId);
            if (skipper.isEmpty()) {
                response.newError("Patrón del barco no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
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
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ResponseDTO response = new ResponseDTO();
            response.newError(e.toString());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            response.newError("Salida previa no encontrada");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.newError(e.toString());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.newMessage("Miembro no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response.newError(e.toString());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
