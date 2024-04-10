package com.nautico.usuarios.controllers;

import com.nautico.usuarios.services.IMemberService;
import com.nautico.usuarios.services.models.dtos.MemberDTO;
import com.nautico.usuarios.services.models.dtos.ResponseDTO;
import com.nautico.usuarios.services.models.dtos.ShipDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/member")
public class MemberControllers {

    @Autowired
    IMemberService memberService;

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable Long memberId) {
        return memberService.getMemberById(memberId);
    }

    @GetMapping
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        return memberService.getAllMembers();
    }

    @GetMapping("/{memberId}/ships")
    public ResponseEntity<List<ShipDTO>> getMemberShips(@PathVariable Long memberId) {
        return memberService.getMemberShips(memberId);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<ResponseDTO> setAsMember(@PathVariable Long userId) {
        return memberService.addMember(userId);
    }

    /*@PutMapping("/{userId}")
    public ResponseEntity<ResponseDTO> updatePartner(@PathVariable Long userId, @RequestBody UserEntity updatedUser) {
        return userService.updateUser(userId, updatedUser);
    }*/

    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseDTO> unsetAsMember(@PathVariable Long userId) {
        return memberService.removeMemberByUserId(userId);
    }
}
