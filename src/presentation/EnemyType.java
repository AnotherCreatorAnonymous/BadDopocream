package presentation;

/**
 * Enumeración de tipos de enemigos
 */
public enum EnemyType {
    TROLL("Troll"),
    MACETA("Pot"),
    CALAMAR("YellowSquid"),
    NARVAL("Narval");
    
    private final String folderName;
    
    EnemyType(String folderName) {
        this.folderName = folderName;
    }
    
    public String getFolderName() {
        return folderName;
    }
}
