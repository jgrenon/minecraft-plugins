package avx.aibuilder.generators;

import avx.aibuilder.data.BuildRequest;
import avx.aibuilder.managers.BuildManager.BlockPlacement;
import org.bukkit.Location;
import org.bukkit.Material;
import java.util.ArrayList;
import java.util.List;

public class WallGenerator implements StructureGenerator {
    @Override
    public List<BlockPlacement> generateStructure(BuildRequest request) {
        List<BlockPlacement> placements = new ArrayList<>();
        Location base = request.getLocation();
        Material material = request.getMaterial().getMaterial();
        int length = request.getSize();
        int height = Math.max(3, length / 4);
        
        for (int x = 0; x < length; x++) {
            for (int y = 0; y < height; y++) {
                placements.add(new BlockPlacement(base.clone().add(x, y, 0), material));
            }
        }
        return placements;
    }
} 