package avx.aibuilder.data;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BuildRequest {
    private UUID id;
    private Player player;
    private String originalPrompt;
    private StructureType structureType;
    private BuildingMaterial material;
    private int size;
    private String style;
    private Location location;
    private int complexity;
    private long timestamp;
    private BuildStatus status;
    private BuildRecipe buildRecipe; // OpenAI generated recipe
    
    public BuildRequest() {
        this.id = UUID.randomUUID();
        this.timestamp = System.currentTimeMillis();
        this.status = BuildStatus.PENDING;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    public String getOriginalPrompt() {
        return originalPrompt;
    }
    
    public void setOriginalPrompt(String originalPrompt) {
        this.originalPrompt = originalPrompt;
    }
    
    public StructureType getStructureType() {
        return structureType;
    }
    
    public void setStructureType(StructureType structureType) {
        this.structureType = structureType;
    }
    
    public BuildingMaterial getMaterial() {
        return material;
    }
    
    public void setMaterial(BuildingMaterial material) {
        this.material = material;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public String getStyle() {
        return style;
    }
    
    public void setStyle(String style) {
        this.style = style;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }
    
    public int getComplexity() {
        return complexity;
    }
    
    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public BuildStatus getStatus() {
        return status;
    }
    
    public void setStatus(BuildStatus status) {
        this.status = status;
    }
    
    public BuildRecipe getBuildRecipe() {
        return buildRecipe;
    }
    
    public void setBuildRecipe(BuildRecipe buildRecipe) {
        this.buildRecipe = buildRecipe;
    }
    
    public boolean hasAdvancedRecipe() {
        return buildRecipe != null;
    }
    
    public enum BuildStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        FAILED
    }
} 