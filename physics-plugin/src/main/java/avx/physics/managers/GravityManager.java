package avx.physics.managers;

import avx.physics.PhysicsPlugin;
import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class GravityManager {
    
    private final PhysicsPlugin plugin;
    private final Map<Location, Double> gravityZones;
    
    public GravityManager(PhysicsPlugin plugin) {
        this.plugin = plugin;
        this.gravityZones = new HashMap<>();
    }
    
    /**
     * Set gravity strength for a specific location
     */
    public void setGravityZone(Location center, double radius, double strength) {
        // Store the gravity zone
        Location key = center.clone();
        key.set(key.getBlockX(), key.getBlockY(), key.getBlockZ()); // Normalize to block coordinates
        gravityZones.put(key, strength);
        
        plugin.getLogger().info("Created gravity zone at " + center + " with strength " + strength);
    }
    
    /**
     * Get gravity strength at a specific location
     */
    public double getGravityStrength(Location location) {
        // Check for gravity zones first
        for (Map.Entry<Location, Double> zone : gravityZones.entrySet()) {
            Location zoneCenter = zone.getKey();
            if (location.getWorld().equals(zoneCenter.getWorld()) && 
                location.distance(zoneCenter) <= 10) { // Default radius of 10 blocks
                return zone.getValue();
            }
        }
        
        // Return default gravity strength
        return plugin.getConfigManager().getDefaultGravityStrength();
    }
    
    /**
     * Apply custom gravity to a falling block
     */
    public void applyGravity(FallingBlock fallingBlock) {
        Location location = fallingBlock.getLocation();
        double gravityStrength = getGravityStrength(location);
        
        Vector velocity = fallingBlock.getVelocity();
        
        // Apply gravity strength modifier
        velocity.setY(velocity.getY() * gravityStrength);
        
        // Apply air resistance if enabled
        double airResistance = plugin.getConfigManager().getAirResistance();
        if (airResistance > 0) {
            velocity.multiply(1.0 - airResistance);
        }
        
        fallingBlock.setVelocity(velocity);
    }
    
    /**
     * Remove gravity zone at location
     */
    public void removeGravityZone(Location location) {
        Location key = location.clone();
        key.set(key.getBlockX(), key.getBlockY(), key.getBlockZ());
        gravityZones.remove(key);
    }
    
    /**
     * Clear all gravity zones
     */
    public void clearAllZones() {
        gravityZones.clear();
    }
    
    /**
     * Get all gravity zones
     */
    public Map<Location, Double> getGravityZones() {
        return new HashMap<>(gravityZones);
    }
    
    /**
     * Check if location is in a custom gravity zone
     */
    public boolean isInGravityZone(Location location) {
        return getGravityStrength(location) != plugin.getConfigManager().getDefaultGravityStrength();
    }
} 