package avx.physics.listeners;

import avx.physics.PhysicsPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;

public class BlockPhysicsListener implements Listener {
    
    private final PhysicsPlugin plugin;
    
    public BlockPhysicsListener(PhysicsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        
        // Check if we should process physics for this world
        if (!plugin.getConfigManager().isWorldEnabled(block.getWorld().getName())) {
            return;
        }
        
        // Add surrounding blocks to physics check queue
        checkSurroundingBlocks(block);
        
        // Invalidate structure cache for this area
        plugin.getStructureManager().invalidateCache(block.getLocation());
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        
        // Check if we should process physics for this world
        if (!plugin.getConfigManager().isWorldEnabled(block.getWorld().getName())) {
            return;
        }
        
        // Invalidate structure cache for this area
        plugin.getStructureManager().invalidateCache(block.getLocation());
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        Block block = event.getBlock();
        
        // Check if we should process physics for this world
        if (!plugin.getConfigManager().isWorldEnabled(block.getWorld().getName())) {
            return;
        }
        
        // Check all destroyed blocks and their surroundings
        for (Block destroyedBlock : event.blockList()) {
            checkSurroundingBlocks(destroyedBlock);
            plugin.getStructureManager().invalidateCache(destroyedBlock.getLocation());
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        // Check if we should process physics for this world
        if (!plugin.getConfigManager().isWorldEnabled(event.getLocation().getWorld().getName())) {
            return;
        }
        
        // Check all destroyed blocks and their surroundings
        for (Block destroyedBlock : event.blockList()) {
            checkSurroundingBlocks(destroyedBlock);
            plugin.getStructureManager().invalidateCache(destroyedBlock.getLocation());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        
        // Don't interfere with vanilla physics for certain blocks
        if (isVanillaPhysicsBlock(block.getType())) {
            return;
        }
        
        // Check if this block should have custom physics
        if (plugin.getConfigManager().getPhysicsBlocks().contains(block.getType())) {
            // Cancel vanilla physics for this block
            event.setCancelled(true);
            
            // Add to our physics queue
            plugin.getPhysicsManager().addPendingPhysicsCheck(block.getLocation());
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        // Handle liquid flow affecting structure
        Block to = event.getToBlock();
        
        if (plugin.getConfigManager().getPhysicsBlocks().contains(to.getType())) {
            plugin.getPhysicsManager().addPendingPhysicsCheck(to.getLocation());
        }
    }
    
    private void checkSurroundingBlocks(Block centerBlock) {
        // Check blocks in a 3x3x3 area around the broken block
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue; // Skip center block
                    
                    Block relative = centerBlock.getRelative(x, y, z);
                    
                    // Only check blocks that have physics enabled
                    if (plugin.getConfigManager().getPhysicsBlocks().contains(relative.getType())) {
                        plugin.getPhysicsManager().addPendingPhysicsCheck(relative.getLocation());
                    }
                }
            }
        }
    }
    
    private boolean isVanillaPhysicsBlock(Material material) {
        // Let vanilla Minecraft handle these blocks
        return material == Material.SAND ||
               material == Material.RED_SAND ||
               material == Material.GRAVEL ||
               material == Material.ANVIL ||
               material == Material.CHIPPED_ANVIL ||
               material == Material.DAMAGED_ANVIL ||
               material == Material.DRAGON_EGG ||
               material == Material.POINTED_DRIPSTONE ||
               material == Material.SCAFFOLDING ||
               material.name().contains("CONCRETE_POWDER") ||
               material.name().contains("_FALLING");
    }
} 