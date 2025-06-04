package avx.physics.listeners;

import avx.physics.PhysicsPlugin;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class EntityListener implements Listener {
    
    private final PhysicsPlugin plugin;
    
    public EntityListener(PhysicsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onFallingBlockSpawn(EntitySpawnEvent event) {
        if (event.getEntityType() != EntityType.FALLING_BLOCK) {
            return;
        }
        
        FallingBlock fallingBlock = (FallingBlock) event.getEntity();
        
        // Apply custom gravity to our physics-controlled falling blocks
        plugin.getGravityManager().applyGravity(fallingBlock);
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onFallingBlockLand(EntityChangeBlockEvent event) {
        if (event.getEntityType() != EntityType.FALLING_BLOCK) {
            return;
        }
        
        FallingBlock fallingBlock = (FallingBlock) event.getEntity();
        
        // Check if momentum/bouncing is enabled
        if (plugin.getConfigManager().isMomentumEnabled()) {
            plugin.getPhysicsManager().handleBlockLanding(fallingBlock, event.getBlock().getLocation());
        }
        
        // Check surrounding blocks for chain reactions
        if (plugin.getConfigManager().isChainReactionsEnabled()) {
            // Small delay to let the block settle first
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                checkSurroundingForPhysics(event.getBlock().getLocation());
            }, 2L);
        }
    }
    
    private void checkSurroundingForPhysics(org.bukkit.Location location) {
        // Check blocks that might be affected by the impact
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    
                    org.bukkit.Location checkLoc = location.clone().add(x, y, z);
                    org.bukkit.block.Block block = checkLoc.getBlock();
                    
                    if (plugin.getConfigManager().getPhysicsBlocks().contains(block.getType())) {
                        // Add delay based on distance for realistic chain reaction
                        int delay = (int) Math.sqrt(x*x + y*y + z*z);
                        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                            plugin.getPhysicsManager().addPendingPhysicsCheck(checkLoc);
                        }, delay);
                    }
                }
            }
        }
    }
} 