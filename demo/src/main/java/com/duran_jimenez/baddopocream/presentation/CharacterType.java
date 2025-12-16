package com.duran_jimenez.baddopocream.presentation;

/**
 * Enumeración de personajes disponibles
 */
public enum CharacterType {
    FRESA("Fresa"),
    VAINILLA("Vainilla"),
    CHOCOLATE("Chocolate");
    
    private final String folderName;
    
    CharacterType(String folderName) {
        this.folderName = folderName;
    }
    
    public String getFolderName() {
        return folderName;
    }
}
