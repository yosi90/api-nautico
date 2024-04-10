package com.nautico.usuarios.services.models.dtos;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ResponseDTO {

    private Integer numberOfErrors = 0;
    private final List<String> messages = new ArrayList<>();

    public ResponseDTO(){ }

    public ResponseDTO(String errorMessage){
        messages.add(errorMessage);
        numberOfErrors++;
    }

    public void newMessage(String message) { this.messages.add(message); }

    public void newError(String message) {

        this.messages.add(message);
        numberOfErrors++;
    }
}
