package avx.aibuilder.managers;

import avx.aibuilder.AIBuilderPlugin;
import avx.aibuilder.data.BuildRequest;
import avx.aibuilder.data.StructureType;
import avx.aibuilder.generators.*;
import avx.aibuilder.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BuildManager {
    
    private final AIBuilderPlugin plugin;
    private final Map<UUID, BuildRequest> activeBuildRequests;
    private final Map<UUID, BukkitTask> buildTasks;
    private final Map<StructureType, StructureGenerator> generators;
    
    public BuildManager(AIBuilderPlugin plugin) {
        this.plugin = plugin;
        this.activeBuildRequests = new ConcurrentHashMap<>();
        this.buildTasks = new ConcurrentHashMap<>();
        this.generators = new HashMap<>();
        
        initializeGenerators();
    }
    
    private void initializeGenerators() {
        generators.put(StructureType.HOUSE, new HouseGenerator());
        generators.put(StructureType.CASTLE, new CastleGenerator());
        generators.put(StructureType.TOWER, new TowerGenerator());
        generators.put(StructureType.BRIDGE, new BridgeGenerator());
        generators.put(StructureType.WALL, new WallGenerator());
        generators.put(StructureType.PYRAMID, new PyramidGenerator());
        generators.put(StructureType.DOME, new DomeGenerator());
        generators.put(StructureType.TREE, new TreeGenerator());
        generators.put(StructureType.GARDEN, new GardenGenerator());
        generators.put(StructureType.ROAD, new RoadGenerator());
    }
    
    public void startBuild(BuildRequest request) {
        if (activeBuildRequests.containsKey(request.getId())) {
            MessageUtils.sendError(request.getPlayer(), "A build is already in progress!");
            return;
        }
        
        // Check if player has necessary permissions and resources
        if (!canPlayerBuild(request)) {
            MessageUtils.sendError(request.getPlayer(), "You don't have permission to build this structure!");
            return;
        }
        
        // Add to active builds
        activeBuildRequests.put(request.getId(), request);
        request.setStatus(BuildRequest.BuildStatus.IN_PROGRESS);
        
        // Send confirmation message
        String description = plugin.getAIManager().generateBuildDescription(request);
        MessageUtils.sendSuccess(request.getPlayer(), "Starting build: " + description);
        
        // Start the building process
        BukkitTask task = Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                executeBuild(request);
            } catch (Exception e) {
                handleBuildError(request, e);
            }
        });
        
        buildTasks.put(request.getId(), task);
    }
    
    private void executeBuild(BuildRequest request) {
        StructureGenerator generator;
        
        // Use AI Recipe Generator if available
        if (request.hasAdvancedRecipe()) {
            generator = new AIRecipeGenerator(request.getBuildRecipe());
            MessageUtils.sendInfo(request.getPlayer(), "Using advanced AI recipe: " + request.getBuildRecipe().getName());
        } else {
            generator = generators.get(request.getStructureType());
            if (generator == null) {
                MessageUtils.sendError(request.getPlayer(), "No generator available for " + request.getStructureType().getDisplayName());
                finalizeBuild(request, false);
                return;
            }
        }
        
        try {
            // Generate the structure blocks
            List<BlockPlacement> placements = generator.generateStructure(request);
            
            // Place blocks gradually for smooth building effect
            placeBuildingBlocks(request, placements);
            
        } catch (Exception e) {
            plugin.getLogger().severe("Error during build execution: " + e.getMessage());
            finalizeBuild(request, false);
        }
    }
    
    private void placeBuildingBlocks(BuildRequest request, List<BlockPlacement> placements) {
        final int[] placedBlocks = {0};
        final int totalBlocks = placements.size();
        final int blocksPerTick = Math.max(1, Math.min(10, totalBlocks / 60)); // Spread over ~3 seconds
        
        BukkitTask placementTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (int i = 0; i < blocksPerTick && placedBlocks[0] < totalBlocks; i++) {
                BlockPlacement placement = placements.get(placedBlocks[0]);
                
                // Ensure we're on the main thread for block placement
                Block block = placement.getLocation().getBlock();
                block.setType(placement.getMaterial());
                
                placedBlocks[0]++;
            }
            
            // Update progress
            if (placedBlocks[0] % 20 == 0 || placedBlocks[0] >= totalBlocks) {
                int progress = (placedBlocks[0] * 100) / totalBlocks;
                MessageUtils.sendInfo(request.getPlayer(), "Build progress: " + progress + "%");
            }
            
            // Check completion
            if (placedBlocks[0] >= totalBlocks) {
                finalizeBuild(request, true);
                Bukkit.getScheduler().cancelTask(buildTasks.get(request.getId()).getTaskId());
            }
            
        }, 1L, 1L); // Run every tick
        
        buildTasks.put(request.getId(), placementTask);
    }
    
    private void finalizeBuild(BuildRequest request, boolean success) {
        // Remove from active builds
        activeBuildRequests.remove(request.getId());
        buildTasks.remove(request.getId());
        
        // Update status
        request.setStatus(success ? BuildRequest.BuildStatus.COMPLETED : BuildRequest.BuildStatus.FAILED);
        
        // Send completion message
        if (success) {
            MessageUtils.sendSuccess(request.getPlayer(), "Build completed successfully!");
            MessageUtils.sendInfo(request.getPlayer(), "Your " + request.getStructureType().getDisplayName() + " is ready!");
        } else {
            MessageUtils.sendError(request.getPlayer(), "Build failed. Please try again.");
        }
    }
    
    private void handleBuildError(BuildRequest request, Exception e) {
        plugin.getLogger().severe("Build error for " + request.getPlayer().getName() + ": " + e.getMessage());
        MessageUtils.sendError(request.getPlayer(), "An error occurred during building: " + e.getMessage());
        finalizeBuild(request, false);
    }
    
    private boolean canPlayerBuild(BuildRequest request) {
        // Check basic permissions
        if (!request.getPlayer().hasPermission("aibuilder.build")) {
            return false;
        }
        
        // Check size limits
        if (!request.getPlayer().hasPermission("aibuilder.unlimited")) {
            int maxSize = plugin.getConfigManager().getMaxBuildSize();
            if (request.getSize() > maxSize) {
                MessageUtils.sendError(request.getPlayer(), "Build size too large! Maximum: " + maxSize);
                return false;
            }
        }
        
        // Check build cooldown
        if (!request.getPlayer().hasPermission("aibuilder.bypass")) {
            // TODO: Implement cooldown checking
        }
        
        return true;
    }
    
    public void cancelBuild(UUID buildId) {
        BuildRequest request = activeBuildRequests.get(buildId);
        if (request != null) {
            request.setStatus(BuildRequest.BuildStatus.CANCELLED);
            
            BukkitTask task = buildTasks.get(buildId);
            if (task != null) {
                task.cancel();
            }
            
            activeBuildRequests.remove(buildId);
            buildTasks.remove(buildId);
            
            MessageUtils.sendInfo(request.getPlayer(), "Build cancelled.");
        }
    }
    
    public void cancelAllBuilds() {
        for (UUID buildId : new HashSet<>(activeBuildRequests.keySet())) {
            cancelBuild(buildId);
        }
    }
    
    public List<BuildRequest> getActiveBuilds() {
        return new ArrayList<>(activeBuildRequests.values());
    }
    
    public BuildRequest getActiveBuild(UUID playerId) {
        return activeBuildRequests.values().stream()
                .filter(request -> request.getPlayer().getUniqueId().equals(playerId))
                .findFirst()
                .orElse(null);
    }
    
    // Inner class for block placement
    public static class BlockPlacement {
        private final Location location;
        private final Material material;
        
        public BlockPlacement(Location location, Material material) {
            this.location = location;
            this.material = material;
        }
        
        public Location getLocation() {
            return location;
        }
        
        public Material getMaterial() {
            return material;
        }
    }
} 