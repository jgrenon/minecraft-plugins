package avx.physics.data;

import org.bukkit.Location;
import org.bukkit.Material;

public class PhysicsBlock {
    
    private final Location originalLocation;
    private final Material material;
    private final long fallTime;
    
    public PhysicsBlock(Location originalLocation, Material material, long fallTime) {
        this.originalLocation = originalLocation.clone();
        this.material = material;
        this.fallTime = fallTime;
    }
    
    public Location getOriginalLocation() {
        return originalLocation.clone();
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public long getFallTime() {
        return fallTime;
    }
    
    public long getAge() {
        return System.currentTimeMillis() - fallTime;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        PhysicsBlock that = (PhysicsBlock) obj;
        return fallTime == that.fallTime &&
               originalLocation.equals(that.originalLocation) &&
               material == that.material;
    }
    
    @Override
    public int hashCode() {
        int result = originalLocation.hashCode();
        result = 31 * result + material.hashCode();
        result = 31 * result + Long.hashCode(fallTime);
        return result;
    }
    
    @Override
    public String toString() {
        return "PhysicsBlock{" +
               "location=" + originalLocation +
               ", material=" + material +
               ", age=" + getAge() + "ms" +
               '}';
    }
} 