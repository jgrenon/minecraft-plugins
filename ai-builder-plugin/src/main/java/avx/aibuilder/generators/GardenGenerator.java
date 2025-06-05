package avx.aibuilder.generators;

import avx.aibuilder.data.BuildRequest;
import avx.aibuilder.managers.BuildManager.BlockPlacement;
import org.bukkit.Location;
import org.bukkit.Material;
import java.util.ArrayList;
import java.util.List;

public class GardenGenerator implements StructureGenerator {
    @Override
    public List<BlockPlacement> generateStructure(BuildRequest request) {
        List<BlockPlacement> placements = new ArrayList<>();
        Location base = request.getLocation();
        int size = request.getSize();
        
        // Create garden area with grass and flowers
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                placements.add(new BlockPlacement(base.clone().add(x, 0, z), Material.GRASS_BLOCK));
                if (Math.random() > 0.7) {
                    Material flower = Math.random() > 0.5 ? Material.POPPY : Material.DANDELION;
                    placements.add(new BlockPlacement(base.clone().add(x, 1, z), flower));
                }
            }
        }
        return placements;
    }
} 