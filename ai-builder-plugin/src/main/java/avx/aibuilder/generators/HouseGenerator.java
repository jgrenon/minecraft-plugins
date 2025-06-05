package avx.aibuilder.generators;

import avx.aibuilder.data.BuildRequest;
import avx.aibuilder.managers.BuildManager.BlockPlacement;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class HouseGenerator implements StructureGenerator {
    
    @Override
    public List<BlockPlacement> generateStructure(BuildRequest request) {
        List<BlockPlacement> placements = new ArrayList<>();
        Location baseLocation = request.getLocation();
        Material wallMaterial = request.getMaterial().getMaterial();
        Material roofMaterial = getRoofMaterial(wallMaterial);
        Material floorMaterial = getFloorMaterial(wallMaterial);
        
        int size = request.getSize();
        int height = Math.max(3, size / 3); // Height proportional to size
        
        // Build foundation/floor
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                Location floorLoc = baseLocation.clone().add(x, -1, z);
                placements.add(new BlockPlacement(floorLoc, floorMaterial));
            }
        }
        
        // Build walls
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < size; x++) {
                for (int z = 0; z < size; z++) {
                    // Only place blocks on the perimeter (walls)
                    if (x == 0 || x == size - 1 || z == 0 || z == size - 1) {
                        Location wallLoc = baseLocation.clone().add(x, y, z);
                        
                        // Add door opening
                        if (x == 0 && z == size / 2 && y < 2) {
                            continue; // Skip door blocks
                        }
                        
                        // Add windows
                        if (y == height / 2 && ((x == size - 1 && z == size / 4) || 
                                               (z == size - 1 && x == size / 4))) {
                            placements.add(new BlockPlacement(wallLoc, Material.GLASS));
                        } else {
                            placements.add(new BlockPlacement(wallLoc, wallMaterial));
                        }
                    }
                }
            }
        }
        
        // Build roof
        buildRoof(placements, baseLocation, size, height, roofMaterial);
        
        // Add door
        Location doorLoc = baseLocation.clone().add(0, 0, size / 2);
        placements.add(new BlockPlacement(doorLoc, Material.OAK_DOOR));
        
        return placements;
    }
    
    private void buildRoof(List<BlockPlacement> placements, Location baseLocation, int size, int height, Material roofMaterial) {
        int roofHeight = Math.max(2, size / 4);
        
        for (int layer = 0; layer < roofHeight; layer++) {
            int roofSize = size - (layer * 2);
            if (roofSize <= 0) break;
            
            int offset = layer;
            for (int x = 0; x < roofSize; x++) {
                for (int z = 0; z < roofSize; z++) {
                    Location roofLoc = baseLocation.clone().add(x + offset, height + layer, z + offset);
                    placements.add(new BlockPlacement(roofLoc, roofMaterial));
                }
            }
        }
    }
    
    private Material getRoofMaterial(Material wallMaterial) {
        // Choose appropriate roof material based on wall material
        if (wallMaterial == Material.STONE || wallMaterial == Material.STONE_BRICKS) {
            return Material.STONE_BRICK_STAIRS;
        } else if (wallMaterial == Material.BRICKS) {
            return Material.BRICK_STAIRS;
        } else if (wallMaterial == Material.SANDSTONE) {
            return Material.SANDSTONE_STAIRS;
        } else {
            return Material.OAK_STAIRS; // Default wooden roof
        }
    }
    
    private Material getFloorMaterial(Material wallMaterial) {
        // Choose appropriate floor material
        if (wallMaterial == Material.STONE || wallMaterial == Material.STONE_BRICKS) {
            return Material.STONE;
        } else if (wallMaterial == Material.BRICKS) {
            return Material.BRICKS;
        } else if (wallMaterial == Material.SANDSTONE) {
            return Material.SANDSTONE;
        } else {
            return Material.OAK_PLANKS; // Default wooden floor
        }
    }
    
    @Override
    public int getEstimatedBlockCount(BuildRequest request) {
        int size = request.getSize();
        int height = Math.max(3, size / 3);
        
        // Foundation + walls + roof
        int foundation = size * size;
        int walls = (size * 4 - 4) * height; // Perimeter walls
        int roof = size * size / 2; // Rough roof estimate
        
        return foundation + walls + roof;
    }
} 