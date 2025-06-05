package avx.aibuilder.generators;

import avx.aibuilder.data.BuildRequest;
import avx.aibuilder.managers.BuildManager.BlockPlacement;
import org.bukkit.Location;
import org.bukkit.Material;
import java.util.ArrayList;
import java.util.List;

public class RoadGenerator implements StructureGenerator {
    @Override
    public List<BlockPlacement> generateStructure(BuildRequest request) {
        List<BlockPlacement> placements = new ArrayList<>();
        Location base = request.getLocation();
        Material material = request.getMaterial().getMaterial();
        int length = request.getSize();
        
        // Create road
        for (int x = 0; x < length; x++) {
            for (int z = 0; z < 3; z++) {
                placements.add(new BlockPlacement(base.clone().add(x, 0, z), material));
            }
        }
        return placements;
    }
} 