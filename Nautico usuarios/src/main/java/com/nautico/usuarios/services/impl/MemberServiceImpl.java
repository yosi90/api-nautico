package com.nautico.usuarios.services.impl;

import com.nautico.usuarios.persistence.entities.MemberEntity;
import com.nautico.usuarios.persistence.entities.ShipEntity;
import com.nautico.usuarios.persistence.entities.UserEntity;
import com.nautico.usuarios.persistence.repositories.MemberRepository;
import com.nautico.usuarios.persistence.repositories.UserRepository;
import com.nautico.usuarios.services.IMemberService;
import com.nautico.usuarios.services.models.dtos.MemberDTO;
import com.nautico.usuarios.services.models.dtos.ResponseDTO;
import com.nautico.usuarios.services.models.dtos.ShipDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MemberServiceImpl implements IMemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<MemberDTO> getMemberById(Long memberId) {
        MemberEntity partner = memberRepository.findById(memberId).orElse(null);
        if (partner == null)
            return new ResponseEntity<>(new MemberDTO(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(new MemberDTO(partner), HttpStatus.FOUND);
    }

    @Override
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        List<MemberEntity> members = memberRepository.findAll();
        List<MemberDTO> memberDTOS = new ArrayList<>();
        if (members.isEmpty())
            return new ResponseEntity<>(memberDTOS, HttpStatus.NO_CONTENT);
        for (MemberEntity memberEntity : members)
            memberDTOS.add(memberEntity.ToDTO());
        return new ResponseEntity<>(memberDTOS, HttpStatus.FOUND);
    }

    @Override
    public ResponseEntity<List<ShipDTO>> getMemberShips(Long memberId) {
        MemberEntity member = memberRepository.findById(memberId).orElse(null);
        if (member == null)
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        if(member.getShips().isEmpty())
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(member.getShipDTOs(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseDTO> addMember(Long userId) {
        ResponseDTO response = new ResponseDTO();
        try {
            Optional<UserEntity> user = memberRepository.findUserByUserId(userId);
            if (user.isPresent()) {
                response.newError("Usuario ya dado de alta como miembro");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
            user = userRepository.findById(userId);
            if (user.isPresent()) {
                MemberEntity member = new MemberEntity();
                member.setUser(user.get());
                memberRepository.save(member);
                response.newMessage("El usuario ahora es miembro");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.newError("Usuario no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response.newError(e.toString());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*@Override
    public ResponseEntity<ResponseDTO> removePartner(UserEntity user) {
        return remove(partnerRepository.findByUser(user));
    }*/

    @Override
    public ResponseEntity<ResponseDTO> removeMemberByUserId(Long userId) {
        return remove(memberRepository.findByUserId(userId));
    }

    @Override
    public ResponseEntity<ResponseDTO> removeMember(Long memberId) {
        return remove(memberRepository.findById(memberId));
    }

    private ResponseEntity<ResponseDTO> remove(Optional<MemberEntity> memberEntity) {
        ResponseDTO response = new ResponseDTO();
        try {
            if (memberEntity.isPresent()) {
                MemberEntity member = memberEntity.get();
                member.setUser(null);
                memberRepository.save(member);
                memberRepository.deleteByMemberId(member.getId());
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
