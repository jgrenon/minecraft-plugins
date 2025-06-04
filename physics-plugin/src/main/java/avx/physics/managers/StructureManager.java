package avx.physics.managers;

import avx.physics.PhysicsPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.*;

public class StructureManager {
    
    private final PhysicsPlugin plugin;
    private final Map<Location, Set<Location>> supportCache;
    private final long cacheExpiration = 30000; // 30 seconds
    private final Map<Location, Long> cacheTimestamps;
    
    public StructureManager(PhysicsPlugin plugin) {
        this.plugin = plugin;
        this.supportCache = new HashMap<>();
        this.cacheTimestamps = new HashMap<>();
    }
    
    /**
     * Check if a block has sufficient structural support
     */
    public boolean hasStructuralSupport(Location location) {
        Block block = location.getBlock();
        
        // Air blocks don't need support
        if (block.getType() == Material.AIR) {
            return true;
        }
        
        // Support blocks are always supported
        if (plugin.getConfigManager().getSupportBlocks().contains(block.getType())) {
            return true;
        }
        
        // Check cache first
        if (isCacheValid(location)) {
            Set<Location> cachedSupports = supportCache.get(location);
            return cachedSupports != null && !cachedSupports.isEmpty();
        }
        
        // Calculate support
        Set<Location> supportingBlocks = findSupportingBlocks(location);
        
        // Cache the result
        supportCache.put(location.clone(), new HashSet<>(supportingBlocks));
        cacheTimestamps.put(location.clone(), System.currentTimeMillis());
        
        // Check if we have minimum required support
        int minSupport = plugin.getConfigManager().getMinSupportBlocks();
        return supportingBlocks.size() >= minSupport;
    }
    
    /**
     * Find all blocks that provide structural support to the given location
     */
    private Set<Location> findSupportingBlocks(Location location) {
        Set<Location> supportingBlocks = new HashSet<>();
        Set<Location> visited = new HashSet<>();
        Queue<Location> toCheck = new LinkedList<>();
        
        int maxDistance = plugin.getConfigManager().getMaxSupportDistance();
        World world = location.getWorld();
        
        // Start with the block itself
        toCheck.add(location.clone());
        
        while (!toCheck.isEmpty() && supportingBlocks.size() < 100) { // Limit to prevent infinite loops
            Location current = toCheck.poll();
            
            if (visited.contains(current)) {
                continue;
            }
            visited.add(current.clone());
            
            // Check if this location is too far
            if (current.distance(location) > maxDistance) {
                continue;
            }
            
            Block currentBlock = world.getBlockAt(current);
            
            // If this is a support block, we found support
            if (plugin.getConfigManager().getSupportBlocks().contains(currentBlock.getType())) {
                supportingBlocks.add(current.clone());
                continue;
            }
            
            // If this block is connected to the ground through a continuous path
            if (isConnectedToGround(current, visited, maxDistance)) {
                supportingBlocks.add(current.clone());
                continue;
            }
            
            // Check adjacent blocks for potential support paths
            for (Vector direction : getSupportDirections()) {
                Location adjacent = current.clone().add(direction);
                Block adjacentBlock = world.getBlockAt(adjacent);
                
                // Only follow solid, physics-enabled blocks or support blocks
                if (adjacentBlock.getType() != Material.AIR && 
                    (plugin.getConfigManager().getPhysicsBlocks().contains(adjacentBlock.getType()) ||
                     plugin.getConfigManager().getSupportBlocks().contains(adjacentBlock.getType()))) {
                    
                    if (!visited.contains(adjacent)) {
                        toCheck.add(adjacent);
                    }
                }
            }
        }
        
        return supportingBlocks;
    }
    
    /**
     * Check if a block is connected to the ground (Y=0 or bedrock layer)
     */
    private boolean isConnectedToGround(Location location, Set<Location> visited, int maxDistance) {
        World world = location.getWorld();
        Queue<Location> pathCheck = new LinkedList<>();
        Set<Location> pathVisited = new HashSet<>(visited);
        
        pathCheck.add(location.clone());
        
        while (!pathCheck.isEmpty()) {
            Location current = pathCheck.poll();
            
            if (pathVisited.contains(current)) {
                continue;
            }
            pathVisited.add(current.clone());
            
            // If we're too far from the original location, stop
            if (current.distance(location) > maxDistance) {
                continue;
            }
            
            // If we reached bedrock level or a support block, we have ground connection
            if (current.getBlockY() <= world.getMinHeight() + 5 || 
                plugin.getConfigManager().getSupportBlocks().contains(world.getBlockAt(current).getType())) {
                return true;
            }
            
            // Check downward path
            for (int y = current.getBlockY() - 1; y >= Math.max(world.getMinHeight(), current.getBlockY() - 3); y--) {
                Location below = new Location(world, current.getBlockX(), y, current.getBlockZ());
                Block belowBlock = world.getBlockAt(below);
                
                if (belowBlock.getType() != Material.AIR) {
                    if (plugin.getConfigManager().getSupportBlocks().contains(belowBlock.getType())) {
                        return true;
                    }
                    
                    if (plugin.getConfigManager().getPhysicsBlocks().contains(belowBlock.getType()) && 
                        !pathVisited.contains(below)) {
                        pathCheck.add(below);
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Get directions to check for structural support
     */
    private List<Vector> getSupportDirections() {
        return Arrays.asList(
            new Vector(0, -1, 0),  // Down (most important)
            new Vector(1, 0, 0),   // East
            new Vector(-1, 0, 0),  // West
            new Vector(0, 0, 1),   // South
            new Vector(0, 0, -1),  // North
            new Vector(1, -1, 0),  // Down-East
            new Vector(-1, -1, 0), // Down-West
            new Vector(0, -1, 1),  // Down-South
            new Vector(0, -1, -1), // Down-North
            new Vector(0, 1, 0)    // Up (least important)
        );
    }
    
    /**
     * Check if cached data is still valid
     */
    private boolean isCacheValid(Location location) {
        Long timestamp = cacheTimestamps.get(location);
        if (timestamp == null) {
            return false;
        }
        
        return System.currentTimeMillis() - timestamp < cacheExpiration;
    }
    
    /**
     * Invalidate cache for a location and surrounding area
     */
    public void invalidateCache(Location location) {
        int radius = 3; // Invalidate 3 block radius
        World world = location.getWorld();
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = location.clone().add(x, y, z);
                    supportCache.remove(loc);
                    cacheTimestamps.remove(loc);
                }
            }
        }
    }
    
    /**
     * Get structural integrity percentage for a block (0-100)
     */
    public double getStructuralIntegrity(Location location) {
        if (!hasStructuralSupport(location)) {
            return 0.0;
        }
        
        Set<Location> supports = supportCache.get(location);
        if (supports == null) {
            return 0.0;
        }
        
        int maxPossibleSupports = plugin.getConfigManager().getMaxSupportDistance() * 2;
        return Math.min(100.0, (supports.size() * 100.0) / maxPossibleSupports);
    }
    
    /**
     * Analyze structural integrity of an area
     */
    public Map<Location, Double> analyzeArea(Location center, int radius) {
        Map<Location, Double> analysis = new HashMap<>();
        World world = center.getWorld();
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = center.clone().add(x, y, z);
                    Block block = world.getBlockAt(loc);
                    
                    if (block.getType() != Material.AIR && 
                        plugin.getConfigManager().getPhysicsBlocks().contains(block.getType())) {
                        analysis.put(loc.clone(), getStructuralIntegrity(loc));
                    }
                }
            }
        }
        
        return analysis;
    }
    
    /**
     * Clean up old cache entries
     */
    public void cleanupCache() {
        long currentTime = System.currentTimeMillis();
        List<Location> toRemove = new ArrayList<>();
        
        for (Map.Entry<Location, Long> entry : cacheTimestamps.entrySet()) {
            if (currentTime - entry.getValue() > cacheExpiration) {
                toRemove.add(entry.getKey());
            }
        }
        
        for (Location location : toRemove) {
            supportCache.remove(location);
            cacheTimestamps.remove(location);
        }
    }
    
    public int getCacheSize() {
        return supportCache.size();
    }
} 