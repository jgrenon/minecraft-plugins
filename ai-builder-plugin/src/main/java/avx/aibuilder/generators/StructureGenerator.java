package avx.aibuilder.generators;

import avx.aibuilder.data.BuildRequest;
import avx.aibuilder.managers.BuildManager.BlockPlacement;

import java.util.List;

public interface StructureGenerator {
    /**
     * Generate a list of block placements for the given build request
     * @param request The build request containing all necessary information
     * @return List of block placements to be executed
     */
    List<BlockPlacement> generateStructure(BuildRequest request);
    
    /**
     * Get the estimated number of blocks this structure will use
     * @param request The build request
     * @return Estimated block count
     */
    default int getEstimatedBlockCount(BuildRequest request) {
        return request.getSize() * request.getSize() * 3; // Default estimation
    }
} 