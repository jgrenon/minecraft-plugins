package avx.aibuilder.data;

public enum StructureType {
    HOUSE("House", "A simple residential building"),
    CASTLE("Castle", "A large fortified structure"),
    TOWER("Tower", "A tall vertical structure"),
    BRIDGE("Bridge", "A structure connecting two points"),
    WALL("Wall", "A protective barrier"),
    PYRAMID("Pyramid", "A triangular stepped structure"),
    DOME("Dome", "A rounded vault structure"),
    TREE("Tree", "An organic tree structure"),
    GARDEN("Garden", "A landscaped area with plants"),
    ROAD("Road", "A path for transportation"),
    STATUE("Statue", "A decorative figure"),
    FOUNTAIN("Fountain", "A water feature"),
    BARN("Barn", "A farm building"),
    WINDMILL("Windmill", "A wind-powered structure"),
    LIGHTHOUSE("Lighthouse", "A tall tower with a beacon");
    
    private final String displayName;
    private final String description;
    
    StructureType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
} 