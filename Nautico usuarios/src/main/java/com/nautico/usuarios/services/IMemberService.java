package com.nautico.usuarios.services;

import com.nautico.usuarios.services.models.dtos.MemberDTO;
import com.nautico.usuarios.services.models.dtos.ResponseDTO;
import com.nautico.usuarios.services.models.dtos.ShipDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IMemberService {

    ResponseEntity<MemberDTO> getMemberById(Long memberId);

    ResponseEntity<List<MemberDTO>> getAllMembers();

    ResponseEntity<List<ShipDTO>> getMemberShips(Long memberId);

    ResponseEntity<ResponseDTO> addMember(Long userId);

    ResponseEntity<ResponseDTO> removeMemberByUserId(Long userId);

    ResponseEntity<ResponseDTO> removeMember(Long memberId);
}
