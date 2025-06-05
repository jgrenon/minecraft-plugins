package avx.aibuilder.generators;

import avx.aibuilder.data.BuildRequest;
import avx.aibuilder.managers.BuildManager.BlockPlacement;
import org.bukkit.Location;
import org.bukkit.Material;
import java.util.ArrayList;
import java.util.List;

public class TreeGenerator implements StructureGenerator {
    @Override
    public List<BlockPlacement> generateStructure(BuildRequest request) {
        List<BlockPlacement> placements = new ArrayList<>();
        Location base = request.getLocation();
        int height = Math.max(5, request.getSize() / 2);
        
        // Trunk
        for (int y = 0; y < height; y++) {
            placements.add(new BlockPlacement(base.clone().add(0, y, 0), Material.OAK_LOG));
        }
        
        // Leaves (simple cross pattern)
        int leafRadius = 2;
        for (int x = -leafRadius; x <= leafRadius; x++) {
            for (int z = -leafRadius; z <= leafRadius; z++) {
                for (int y = height - 2; y < height + 2; y++) {
                    if (Math.abs(x) + Math.abs(z) <= leafRadius) {
                        placements.add(new BlockPlacement(base.clone().add(x, y, z), Material.OAK_LEAVES));
                    }
                }
            }
        }
        return placements;
    }
} 