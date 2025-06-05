package avx.aibuilder.generators;

import avx.aibuilder.data.BuildRequest;
import avx.aibuilder.managers.BuildManager.BlockPlacement;
import org.bukkit.Location;
import org.bukkit.Material;
import java.util.ArrayList;
import java.util.List;

public class DomeGenerator implements StructureGenerator {
    @Override
    public List<BlockPlacement> generateStructure(BuildRequest request) {
        List<BlockPlacement> placements = new ArrayList<>();
        Location center = request.getLocation();
        Material material = request.getMaterial().getMaterial();
        int radius = request.getSize() / 2;
        
        // Simple dome using sphere algorithm
        for (int x = -radius; x <= radius; x++) {
            for (int y = 0; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x*x + y*y + z*z);
                    if (distance <= radius && distance >= radius - 1) {
                        placements.add(new BlockPlacement(center.clone().add(x, y, z), material));
                    }
                }
            }
        }
        return placements;
    }
} 