package com.nautico.usuarios.services.models.Entities;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public abstract class DTOCosmicEntity {

    private Map<String, String > URIs = new HashMap<>();

    public void newURI(String title, String URI) {
        URIs.put(title, URI);
    }
}
