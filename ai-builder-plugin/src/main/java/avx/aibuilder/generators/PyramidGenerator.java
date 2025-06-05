package avx.aibuilder.generators;

import avx.aibuilder.data.BuildRequest;
import avx.aibuilder.managers.BuildManager.BlockPlacement;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class PyramidGenerator implements StructureGenerator {
    
    @Override
    public List<BlockPlacement> generateStructure(BuildRequest request) {
        List<BlockPlacement> placements = new ArrayList<>();
        Location baseLocation = request.getLocation();
        Material material = request.getMaterial().getMaterial();
        
        int size = request.getSize();
        int height = size / 2 + 2;
        
        // Build pyramid layer by layer
        for (int y = 0; y < height; y++) {
            int layerSize = size - (y * 2);
            if (layerSize <= 0) break;
            
            int offset = y;
            for (int x = 0; x < layerSize; x++) {
                for (int z = 0; z < layerSize; z++) {
                    Location pyramidLoc = baseLocation.clone().add(x + offset, y, z + offset);
                    placements.add(new BlockPlacement(pyramidLoc, material));
                }
            }
        }
        
        return placements;
    }
} 