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
        try {
            MemberEntity partner = memberRepository.findById(memberId).orElse(null);
            if (partner == null)
                return ResponseEntity.notFound().build();
            return new ResponseEntity<>(new MemberDTO(partner), HttpStatus.FOUND);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        try {
            List<MemberEntity> members = memberRepository.findAll();
            List<MemberDTO> memberDTOS = new ArrayList<>();
            if (members.isEmpty())
                return new ResponseEntity<>(memberDTOS, HttpStatus.NO_CONTENT);
            for (MemberEntity memberEntity : members)
                memberDTOS.add(memberEntity.ToDTO());
            return new ResponseEntity<>(memberDTOS, HttpStatus.FOUND);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<List<ShipDTO>> getMemberShips(Long memberId) {
        try {
            MemberEntity member = memberRepository.findById(memberId).orElse(null);
            if (member == null)
                return ResponseEntity.notFound().build();
            if (member.getShips().isEmpty())
                return ResponseEntity.noContent().build();
            return ResponseEntity.ok(member.getShipDTOs());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> addMember(Long userId) {
        try {
            ResponseDTO response = new ResponseDTO();
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
                return ResponseEntity.ok(response);
            } else
                return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
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

        try {
            ResponseDTO response = new ResponseDTO();
            if (memberEntity.isPresent()) {
                MemberEntity member = memberEntity.get();
                member.setUser(null);
                memberRepository.save(member);
                memberRepository.deleteByMemberId(member.getId());
                response.newMessage("Miembro eliminado");
                return ResponseEntity.ok(response);
            } else
                return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
