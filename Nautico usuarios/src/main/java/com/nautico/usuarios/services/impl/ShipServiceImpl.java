package com.nautico.usuarios.services.impl;

import com.nautico.usuarios.persistence.entities.MemberEntity;
import com.nautico.usuarios.persistence.entities.ShipEntity;
import com.nautico.usuarios.persistence.entities.UserEntity;
import com.nautico.usuarios.persistence.repositories.MemberRepository;
import com.nautico.usuarios.persistence.repositories.ShipRepository;
import com.nautico.usuarios.services.IShipService;
import com.nautico.usuarios.services.models.dtos.DepartureDTO;
import com.nautico.usuarios.services.models.dtos.ResponseDTO;
import com.nautico.usuarios.services.models.dtos.ShipDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShipServiceImpl implements IShipService {

    @Autowired
    private MemberRepository memberRepository;

    private final ShipRepository shipRepository;

    @Autowired
    public ShipServiceImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public ResponseEntity<ShipDTO> getShipById(Long userId) {
        ShipEntity ship = shipRepository.findById(userId).orElse(null);
        if (ship == null) return new ResponseEntity<>(new ShipDTO(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(new ShipDTO(ship), HttpStatus.FOUND);
    }

    @Override
    public ResponseEntity<List<ShipDTO>> getAllShips() {
        List<ShipEntity> ships = shipRepository.findAll();
        List<ShipDTO> shipDTOS = new ArrayList<>();
        if (ships.isEmpty()) return new ResponseEntity<>(shipDTOS, HttpStatus.NO_CONTENT);
        for (ShipEntity shipEntity : ships)
            shipDTOS.add(shipEntity.ToDTO());
        return new ResponseEntity<>(shipDTOS, HttpStatus.FOUND);
    }

    @Override
    public ResponseEntity<List<DepartureDTO>> getDepartures(Long shipId) {
        ShipEntity ship = shipRepository.findById(shipId).orElse(null);
        if (ship == null) return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        if (ship.getDepartures().isEmpty()) return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(ship.getDeparturesDTOs(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseDTO> createShip(ShipEntity shipNew, Long memberId) {
        try {
            ResponseDTO response = new ResponseDTO();
            if (shipRepository.findByRegistration(shipNew.getRegistration()).isPresent()) {
                response.newError("Matrícula de embarcación ya registrada");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
            Optional<MemberEntity> member = memberRepository.findById(memberId);
            if (member.isPresent()) {
                ShipEntity updatableShip = findUpdatableShip(member.get());
                ResponseEntity<ResponseDTO> responseEntity = updateShip(updatableShip.getId(), shipNew);
                responseEntity.getBody().newMessage("Embarcación creada");
                return responseEntity;
            } else {
                response.newError("Miembro no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            ResponseDTO response = new ResponseDTO();
            response.newError(e.toString());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ShipEntity findUpdatableShip(MemberEntity member) {
        int threshold = 3600;
        Optional<ShipEntity> shipUpdatable = shipRepository.findFirstUpdatable(threshold);
        if (shipUpdatable.isEmpty()) {
            ShipEntity shipEntity = new ShipEntity();
            shipEntity.setName("NombreNoValido");
            shipEntity.setRegistration("AA-1111-AA");
            shipEntity.setMooringNumber(0);
            shipEntity.setFee(BigDecimal.valueOf(0));
            shipEntity.setOwner(member);
            shipEntity = shipRepository.save(shipEntity);
            return shipEntity;
        } else return shipUpdatable.get();
    }

    @Override
    public ResponseEntity<ResponseDTO> updateShip(Long previousShipId, ShipEntity updatedShip) {
        ResponseDTO response = new ResponseDTO();
        try {
            ShipEntity previousShip = shipRepository.findById(previousShipId).orElseThrow(() -> new EntityNotFoundException("Embarcación no encontrada"));
            previousShip.setLifeSpan(updatedShip.getLifeSpan().plusYears(100));
            previousShip.setName(updatedShip.getName());
            previousShip.setRegistration(updatedShip.getRegistration());
            previousShip.setMooringNumber(updatedShip.getMooringNumber());
            previousShip.setFee(updatedShip.getFee());
            shipRepository.save(previousShip);
            response.newMessage("Embarcación actualizada");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            response.newError("Id no encontrada");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.newError(e.toString());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> removeShip(Long shipId) {
        return remove(shipRepository.findById(shipId));
    }

    private ResponseEntity<ResponseDTO> remove(Optional<ShipEntity> shipEntity) {
        ResponseDTO response = new ResponseDTO();
        try {
            if (shipEntity.isPresent()) {
                ShipEntity ship = shipEntity.get();
                ship.setOwner(null);
                shipRepository.save(ship);
                shipRepository.deleteByShipId(ship.getId());
                response.newMessage("Embarcación eliminada");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.newMessage("Embarcación no encontrada");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response.newError(e.toString());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
