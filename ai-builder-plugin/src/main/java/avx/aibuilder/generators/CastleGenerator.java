package avx.aibuilder.generators;

import avx.aibuilder.data.BuildRequest;
import avx.aibuilder.managers.BuildManager.BlockPlacement;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class CastleGenerator implements StructureGenerator {
    
    @Override
    public List<BlockPlacement> generateStructure(BuildRequest request) {
        List<BlockPlacement> placements = new ArrayList<>();
        Location baseLocation = request.getLocation();
        Material wallMaterial = request.getMaterial().getMaterial();
        
        int size = request.getSize();
        int wallHeight = Math.max(4, size / 3);
        
        // Build castle walls (hollow rectangle)
        for (int y = 0; y < wallHeight; y++) {
            for (int x = 0; x < size; x++) {
                for (int z = 0; z < size; z++) {
                    if (x == 0 || x == size - 1 || z == 0 || z == size - 1) {
                        Location wallLoc = baseLocation.clone().add(x, y, z);
                        placements.add(new BlockPlacement(wallLoc, wallMaterial));
                    }
                }
            }
        }
        
        // Add corner towers
        int towerHeight = wallHeight + 3;
        int towerSize = 3;
        
        addTower(placements, baseLocation.clone().add(-1, 0, -1), towerSize, towerHeight, wallMaterial);
        addTower(placements, baseLocation.clone().add(size - towerSize + 1, 0, -1), towerSize, towerHeight, wallMaterial);
        addTower(placements, baseLocation.clone().add(-1, 0, size - towerSize + 1), towerSize, towerHeight, wallMaterial);
        addTower(placements, baseLocation.clone().add(size - towerSize + 1, 0, size - towerSize + 1), towerSize, towerHeight, wallMaterial);
        
        // Add gate
        Location gateLoc = baseLocation.clone().add(0, 0, size / 2);
        placements.add(new BlockPlacement(gateLoc, Material.AIR));
        placements.add(new BlockPlacement(gateLoc.clone().add(0, 1, 0), Material.AIR));
        
        return placements;
    }
    
    private void addTower(List<BlockPlacement> placements, Location towerBase, int towerSize, int height, Material material) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < towerSize; x++) {
                for (int z = 0; z < towerSize; z++) {
                    if (x == 0 || x == towerSize - 1 || z == 0 || z == towerSize - 1 || y == 0) {
                        Location towerLoc = towerBase.clone().add(x, y, z);
                        placements.add(new BlockPlacement(towerLoc, material));
                    }
                }
            }
        }
    }
} 