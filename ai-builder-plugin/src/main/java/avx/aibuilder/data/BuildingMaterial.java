package avx.aibuilder.data;

import org.bukkit.Material;

public class BuildingMaterial {
    private final Material material;
    private final String name;
    private final Material accent; // Optional accent material
    
    public BuildingMaterial(Material material, String name) {
        this.material = material;
        this.name = name;
        this.accent = null;
    }
    
    public BuildingMaterial(Material material, String name, Material accent) {
        this.material = material;
        this.name = name;
        this.accent = accent;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public String getName() {
        return name;
    }
    
    public Material getAccent() {
        return accent;
    }
    
    public boolean hasAccent() {
        return accent != null;
    }
    
    @Override
    public String toString() {
        return name;
    }
} 