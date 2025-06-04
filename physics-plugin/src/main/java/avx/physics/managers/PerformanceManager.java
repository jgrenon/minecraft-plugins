package avx.physics.managers;

import avx.physics.PhysicsPlugin;
import org.bukkit.Bukkit;

public class PerformanceManager {
    
    private final PhysicsPlugin plugin;
    private double lastTPS = 20.0;
    private long lastTPSCheck = System.currentTimeMillis();
    private int skipCounter = 0;
    
    public PerformanceManager(PhysicsPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Check if physics can be processed based on server performance
     */
    public boolean canProcessPhysics() {
        updateTPS();
        
        double minTPS = plugin.getConfigManager().getMinTPS();
        
        if (lastTPS < minTPS) {
            skipCounter++;
            // Skip every other tick when TPS is low
            return skipCounter % 2 == 0;
        }
        
        // Reset skip counter when TPS is good
        skipCounter = 0;
        return true;
    }
    
    /**
     * Update the current TPS reading
     */
    private void updateTPS() {
        long currentTime = System.currentTimeMillis();
        
        // Only update TPS every 5 seconds to avoid constant calculation
        if (currentTime - lastTPSCheck > 5000) {
            try {
                // Get server TPS using reflection (Paper/Spigot specific)
                Object server = Bukkit.getServer();
                if (server.getClass().getSimpleName().equals("CraftServer")) {
                    Object minecraftServer = server.getClass().getMethod("getServer").invoke(server);
                    double[] recentTps = (double[]) minecraftServer.getClass().getField("recentTps").get(minecraftServer);
                    lastTPS = recentTps[0]; // 1-minute average
                } else {
                    // Fallback for non-Spigot servers
                    lastTPS = 20.0;
                }
            } catch (Exception e) {
                // Fallback TPS calculation
                lastTPS = Math.min(20.0, 20.0); // Assume normal TPS if can't get real value
            }
            
            lastTPSCheck = currentTime;
            
            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getLogger().info("Current TPS: " + String.format("%.2f", lastTPS));
            }
        }
    }
    
    /**
     * Get the current TPS
     */
    public double getCurrentTPS() {
        updateTPS();
        return lastTPS;
    }
    
    /**
     * Get performance statistics
     */
    public String getPerformanceStats() {
        int pendingChecks = plugin.getPhysicsManager().getPendingChecksCount();
        int fallingBlocks = plugin.getPhysicsManager().getFallingBlocksCount();
        int cacheSize = plugin.getStructureManager().getCacheSize();
        
        return String.format(
            "TPS: %.2f | Pending: %d | Falling: %d | Cache: %d",
            getCurrentTPS(),
            pendingChecks,
            fallingBlocks,
            cacheSize
        );
    }
    
    /**
     * Get recommended settings based on current performance
     */
    public void optimizeSettings() {
        double tps = getCurrentTPS();
        
        if (tps < 15.0) {
            plugin.getLogger().warning("Low TPS detected (" + String.format("%.2f", tps) + "). Consider:");
            plugin.getLogger().warning("- Reducing max-blocks-per-tick in config");
            plugin.getLogger().warning("- Decreasing max-support-distance");
            plugin.getLogger().warning("- Disabling chain-reactions temporarily");
        } else if (tps > 19.5) {
            plugin.getLogger().info("Performance is excellent! You could increase physics processing if desired.");
        }
    }
    
    /**
     * Check if server is under heavy load
     */
    public boolean isServerOverloaded() {
        return getCurrentTPS() < plugin.getConfigManager().getMinTPS();
    }
    
    /**
     * Get load factor (0.0 = no load, 1.0 = maximum load)
     */
    public double getLoadFactor() {
        double tps = getCurrentTPS();
        return Math.max(0.0, Math.min(1.0, (20.0 - tps) / 20.0));
    }
} 