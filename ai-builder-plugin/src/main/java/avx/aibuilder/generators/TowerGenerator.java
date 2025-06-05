package avx.aibuilder.generators;

import avx.aibuilder.data.BuildRequest;
import avx.aibuilder.managers.BuildManager.BlockPlacement;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class TowerGenerator implements StructureGenerator {
    
    @Override
    public List<BlockPlacement> generateStructure(BuildRequest request) {
        List<BlockPlacement> placements = new ArrayList<>();
        Location baseLocation = request.getLocation();
        Material wallMaterial = request.getMaterial().getMaterial();
        
        int baseSize = Math.max(3, request.getSize() / 2); // Tower base
        int height = request.getSize() * 2; // Towers are tall
        
        // Build tower base (solid foundation)
        for (int x = 0; x < baseSize; x++) {
            for (int z = 0; z < baseSize; z++) {
                for (int y = -2; y < 2; y++) {
                    Location baseLoc = baseLocation.clone().add(x, y, z);
                    placements.add(new BlockPlacement(baseLoc, wallMaterial));
                }
            }
        }
        
        // Build tower shaft (hollow cylinder effect)
        for (int y = 2; y < height; y++) {
            boolean isFloorLevel = (y - 2) % 8 == 0; // Floor every 8 blocks
            
            for (int x = 0; x < baseSize; x++) {
                for (int z = 0; z < baseSize; z++) {
                    boolean isWall = x == 0 || x == baseSize - 1 || z == 0 || z == baseSize - 1;
                    boolean isCorner = (x == 0 || x == baseSize - 1) && (z == 0 || z == baseSize - 1);
                    
                    Location towerLoc = baseLocation.clone().add(x, y, z);
                    
                    if (isFloorLevel && !isWall) {
                        // Add floor
                        placements.add(new BlockPlacement(towerLoc, wallMaterial));
                    } else if (isWall) {
                        // Add windows occasionally
                        if (y % 6 == 0 && !isCorner && Math.random() > 0.5) {
                            placements.add(new BlockPlacement(towerLoc, Material.GLASS));
                        } else {
                            placements.add(new BlockPlacement(towerLoc, wallMaterial));
                        }
                    }
                }
            }
        }
        
        // Build tower top (battlements)
        buildBattlements(placements, baseLocation, baseSize, height, wallMaterial);
        
        // Add entrance
        Location entranceLoc = baseLocation.clone().add(0, 0, baseSize / 2);
        placements.add(new BlockPlacement(entranceLoc, Material.AIR));
        entranceLoc.add(0, 1, 0);
        placements.add(new BlockPlacement(entranceLoc, Material.AIR));
        
        return placements;
    }
    
    private void buildBattlements(List<BlockPlacement> placements, Location baseLocation, int baseSize, int height, Material material) {
        // Create crenellated battlements
        for (int x = 0; x < baseSize; x++) {
            for (int z = 0; z < baseSize; z++) {
                boolean isWall = x == 0 || x == baseSize - 1 || z == 0 || z == baseSize - 1;
                
                if (isWall) {
                    // Create merlon pattern (alternating high/low)
                    boolean isHigh = (x + z) % 2 == 0;
                    int battlementHeight = isHigh ? 2 : 1;
                    
                    for (int y = 0; y < battlementHeight; y++) {
                        Location battlementLoc = baseLocation.clone().add(x, height + y, z);
                        placements.add(new BlockPlacement(battlementLoc, material));
                    }
                }
            }
        }
    }
    
    @Override
    public int getEstimatedBlockCount(BuildRequest request) {
        int baseSize = Math.max(3, request.getSize() / 2);
        int height = request.getSize() * 2;
        
        // Base + walls + floors + battlements
        int base = baseSize * baseSize * 4;
        int walls = (baseSize * 4 - 4) * height;
        int floors = (baseSize - 2) * (baseSize - 2) * (height / 8); // Floor every 8 blocks
        int battlements = baseSize * 4;
        
        return base + walls + floors + battlements;
    }
} 