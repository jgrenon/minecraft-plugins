package avx.physics.managers;

import avx.physics.PhysicsPlugin;
import avx.physics.data.PhysicsBlock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PhysicsManager {
    
    private final PhysicsPlugin plugin;
    private final Map<Location, PhysicsBlock> physicsBlocks;
    private final Queue<Location> pendingPhysicsChecks;
    private final Set<Location> fallingBlocks;
    
    public PhysicsManager(PhysicsPlugin plugin) {
        this.plugin = plugin;
        this.physicsBlocks = new ConcurrentHashMap<>();
        this.pendingPhysicsChecks = new ConcurrentLinkedQueue<>();
        this.fallingBlocks = ConcurrentHashMap.newKeySet();
    }
    
    public void processPhysics() {
        int maxBlocks = plugin.getConfigManager().getMaxBlocksPerTick();
        int processed = 0;
        
        while (!pendingPhysicsChecks.isEmpty() && processed < maxBlocks) {
            Location location = pendingPhysicsChecks.poll();
            if (location != null && !fallingBlocks.contains(location)) {
                processBlockPhysics(location);
                processed++;
            }
        }
    }
    
    public void addPendingPhysicsCheck(Location location) {
        if (!fallingBlocks.contains(location)) {
            pendingPhysicsChecks.offer(location.clone());
        }
    }
    
    private void processBlockPhysics(Location location) {
        Block block = location.getBlock();
        
        // Check if block still exists and has physics enabled
        if (block.getType() == Material.AIR || 
            !plugin.getConfigManager().getPhysicsBlocks().contains(block.getType())) {
            return;
        }
        
        // Check if world has physics enabled
        if (!plugin.getConfigManager().isWorldEnabled(block.getWorld().getName())) {
            return;
        }
        
        // Check structural integrity
        if (!plugin.getStructureManager().hasStructuralSupport(location)) {
            makeBlockFall(location);
        }
    }
    
    public void makeBlockFall(Location location) {
        Block block = location.getBlock();
        Material material = block.getType();
        
        if (material == Material.AIR || fallingBlocks.contains(location)) {
            return;
        }
        
        // Mark as falling to prevent duplicate processing
        fallingBlocks.add(location.clone());
        
        // Create falling block entity
        Location spawnLoc = location.clone().add(0.5, 0, 0.5);
        FallingBlock fallingBlock = spawnLoc.getWorld().spawnFallingBlock(spawnLoc, material.createBlockData());
        
        // Apply custom gravity and momentum
        applyCustomPhysics(fallingBlock);
        
        // Show effects
        showFallingEffects(location);
        
        // Remove original block
        block.setType(Material.AIR);
        
        // Store physics data
        PhysicsBlock physicsBlockData = new PhysicsBlock(location, material, System.currentTimeMillis());
        physicsBlocks.put(location, physicsBlockData);
        
        // Check for chain reactions
        if (plugin.getConfigManager().isChainReactionsEnabled()) {
            checkChainReactions(location);
        }
        
        // Schedule removal from falling blocks set when block lands
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            fallingBlocks.remove(location);
            physicsBlocks.remove(location);
        }, 100L); // 5 seconds maximum fall time
    }
    
    private void applyCustomPhysics(FallingBlock fallingBlock) {
        double gravityStrength = plugin.getConfigManager().getDefaultGravityStrength();
        double airResistance = plugin.getConfigManager().getAirResistance();
        
        // Apply custom gravity
        Vector velocity = fallingBlock.getVelocity();
        velocity.setY(velocity.getY() * gravityStrength);
        
        // Apply air resistance
        velocity.multiply(1.0 - airResistance);
        
        fallingBlock.setVelocity(velocity);
        
        // Set bounce if momentum is enabled
        if (plugin.getConfigManager().isMomentumEnabled()) {
            // This will be handled in the EntityListener when the block hits the ground
        }
    }
    
    private void showFallingEffects(Location location) {
        if (plugin.getConfigManager().isFallingParticlesEnabled()) {
            location.getWorld().spawnParticle(
                Particle.BLOCK_CRACK, 
                location.clone().add(0.5, 0.5, 0.5),
                10,
                0.3, 0.3, 0.3,
                0.1,
                location.getBlock().getBlockData()
            );
        }
        
        if (plugin.getConfigManager().isSoundsEnabled()) {
            location.getWorld().playSound(
                location,
                Sound.BLOCK_STONE_BREAK,
                plugin.getConfigManager().getSoundVolume(),
                0.8f + (float)(Math.random() * 0.4) // Random pitch variation
            );
        }
    }
    
    private void checkChainReactions(Location origin) {
        int maxDistance = plugin.getConfigManager().getMaxChainDistance();
        World world = origin.getWorld();
        
        // Check surrounding blocks in a radius
        for (int x = -maxDistance; x <= maxDistance; x++) {
            for (int y = -maxDistance; y <= maxDistance; y++) {
                for (int z = -maxDistance; z <= maxDistance; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    
                    Location checkLocation = origin.clone().add(x, y, z);
                    Block block = world.getBlockAt(checkLocation);
                    
                    // Only check blocks that have physics
                    if (plugin.getConfigManager().getPhysicsBlocks().contains(block.getType())) {
                        // Add to pending checks with a slight delay to create realistic chain reaction
                        int delay = (int) (Math.sqrt(x*x + y*y + z*z) * 2); // Delay based on distance
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            addPendingPhysicsCheck(checkLocation);
                        }, delay);
                    }
                }
            }
        }
    }
    
    public void handleBlockLanding(FallingBlock fallingBlock, Location landLocation) {
        if (!plugin.getConfigManager().isMomentumEnabled()) {
            return;
        }
        
        double bounceFactor = plugin.getConfigManager().getBounceFactor();
        Vector velocity = fallingBlock.getVelocity();
        
        // Calculate bounce
        if (Math.abs(velocity.getY()) > 0.1 && bounceFactor > 0) {
            velocity.setY(-velocity.getY() * bounceFactor);
            fallingBlock.setVelocity(velocity);
            
            // Show impact effects
            showImpactEffects(landLocation);
        }
    }
    
    private void showImpactEffects(Location location) {
        if (plugin.getConfigManager().isImpactParticlesEnabled()) {
            location.getWorld().spawnParticle(
                Particle.CLOUD,
                location.clone().add(0.5, 0.1, 0.5),
                5,
                0.2, 0.1, 0.2,
                0.05
            );
        }
        
        if (plugin.getConfigManager().isSoundsEnabled()) {
            location.getWorld().playSound(
                location,
                Sound.BLOCK_STONE_HIT,
                plugin.getConfigManager().getSoundVolume() * 0.7f,
                1.2f + (float)(Math.random() * 0.3)
            );
        }
    }
    
    public boolean isFalling(Location location) {
        return fallingBlocks.contains(location);
    }
    
    public void clearAll() {
        physicsBlocks.clear();
        pendingPhysicsChecks.clear();
        fallingBlocks.clear();
    }
    
    public int getPendingChecksCount() {
        return pendingPhysicsChecks.size();
    }
    
    public int getFallingBlocksCount() {
        return fallingBlocks.size();
    }
} 