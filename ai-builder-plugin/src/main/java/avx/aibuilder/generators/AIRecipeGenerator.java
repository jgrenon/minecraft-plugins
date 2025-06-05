package avx.aibuilder.generators;

import avx.aibuilder.data.BuildRecipe;
import avx.aibuilder.data.BuildRequest;
import avx.aibuilder.managers.BuildManager.BlockPlacement;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;
import java.util.stream.Collectors;

public class AIRecipeGenerator implements StructureGenerator {
    
    private final BuildRecipe recipe;
    
    public AIRecipeGenerator(BuildRecipe recipe) {
        this.recipe = recipe;
    }
    
    @Override
    public List<BlockPlacement> generateStructure(BuildRequest request) {
        List<BlockPlacement> placements = new ArrayList<>();
        Location baseLocation = request.getLocation();
        
        // Sort components by priority (lower number = higher priority)
        List<BuildRecipe.Component> sortedComponents = recipe.getComponents().stream()
            .sorted(Comparator.comparingInt(BuildRecipe.Component::getPriority))
            .collect(Collectors.toList());
        
        // Build each component in order
        for (BuildRecipe.Component component : sortedComponents) {
            generateComponent(placements, baseLocation, component);
        }
        
        // Add decorative elements
        if (recipe.getDecorativeElements() != null) {
            for (BuildRecipe.DecorativeElement element : recipe.getDecorativeElements()) {
                generateDecorativeElement(placements, baseLocation, element);
            }
        }
        
        return placements;
    }
    
    private void generateComponent(List<BlockPlacement> placements, Location baseLocation, BuildRecipe.Component component) {
        Material material = parseMaterial(component.getMaterial());
        if (material == null) {
            return; // Skip invalid materials
        }
        
        switch (component.getPattern().toUpperCase()) {
            case "SOLID":
                generateSolidComponent(placements, baseLocation, component, material);
                break;
            case "HOLLOW":
                generateHollowComponent(placements, baseLocation, component, material);
                break;
            case "FRAME":
                generateFrameComponent(placements, baseLocation, component, material);
                break;
            case "DECORATIVE":
                generateDecorativeComponent(placements, baseLocation, component, material);
                break;
            case "NATURAL":
                generateNaturalComponent(placements, baseLocation, component, material);
                break;
            default:
                generateSolidComponent(placements, baseLocation, component, material);
        }
    }
    
    private void generateSolidComponent(List<BlockPlacement> placements, Location baseLocation, 
                                       BuildRecipe.Component component, Material material) {
        int width = recipe.getDimensions().getWidth();
        int length = recipe.getDimensions().getLength();
        int height = recipe.getDimensions().getHeight();
        
        // Adjust dimensions based on component type
        switch (component.getName().toLowerCase()) {
            case "foundation":
                generateFoundation(placements, baseLocation, width, length, material);
                break;
            case "walls":
                generateWalls(placements, baseLocation, width, length, height, material);
                break;
            case "roof":
                generateRoof(placements, baseLocation, width, length, height, material);
                break;
            case "floor":
                generateFloor(placements, baseLocation, width, length, 0, material);
                break;
            default:
                // Default solid fill
                generateSolidFill(placements, baseLocation, width, length, height, material);
        }
    }
    
    private void generateHollowComponent(List<BlockPlacement> placements, Location baseLocation,
                                        BuildRecipe.Component component, Material material) {
        int width = recipe.getDimensions().getWidth();
        int length = recipe.getDimensions().getLength();
        int height = recipe.getDimensions().getHeight();
        
        // Generate hollow structure (walls only)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < length; z++) {
                    // Only place blocks on the perimeter
                    if (x == 0 || x == width - 1 || z == 0 || z == length - 1) {
                        Location loc = baseLocation.clone().add(x, y, z);
                        placements.add(new BlockPlacement(loc, material));
                    }
                }
            }
        }
    }
    
    private void generateFrameComponent(List<BlockPlacement> placements, Location baseLocation,
                                       BuildRecipe.Component component, Material material) {
        int width = recipe.getDimensions().getWidth();
        int length = recipe.getDimensions().getLength();
        int height = recipe.getDimensions().getHeight();
        
        // Generate frame structure (corners and edges)
        for (int y = 0; y < height; y++) {
            // Corner posts
            placements.add(new BlockPlacement(baseLocation.clone().add(0, y, 0), material));
            placements.add(new BlockPlacement(baseLocation.clone().add(width - 1, y, 0), material));
            placements.add(new BlockPlacement(baseLocation.clone().add(0, y, length - 1), material));
            placements.add(new BlockPlacement(baseLocation.clone().add(width - 1, y, length - 1), material));
        }
        
        // Top and bottom frames
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                if (x == 0 || x == width - 1 || z == 0 || z == length - 1) {
                    placements.add(new BlockPlacement(baseLocation.clone().add(x, 0, z), material));
                    placements.add(new BlockPlacement(baseLocation.clone().add(x, height - 1, z), material));
                }
            }
        }
    }
    
    private void generateDecorativeComponent(List<BlockPlacement> placements, Location baseLocation,
                                           BuildRecipe.Component component, Material material) {
        // Add decorative patterns or details
        int width = recipe.getDimensions().getWidth();
        int length = recipe.getDimensions().getLength();
        int height = recipe.getDimensions().getHeight();
        
        // Create decorative patterns (example: alternating blocks)
        for (int y = 0; y < height; y += 2) {
            for (int x = 0; x < width; x += 2) {
                for (int z = 0; z < length; z += 2) {
                    if ((x + z + y) % 2 == 0) {
                        Location loc = baseLocation.clone().add(x, y, z);
                        placements.add(new BlockPlacement(loc, material));
                    }
                }
            }
        }
    }
    
    private void generateNaturalComponent(List<BlockPlacement> placements, Location baseLocation,
                                         BuildRecipe.Component component, Material material) {
        // Generate organic, natural-looking patterns
        int width = recipe.getDimensions().getWidth();
        int length = recipe.getDimensions().getLength();
        int height = recipe.getDimensions().getHeight();
        
        Random random = new Random();
        
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                for (int y = 0; y < height; y++) {
                    // Use probability for more natural distribution
                    double distance = Math.sqrt(Math.pow(x - width/2.0, 2) + Math.pow(z - length/2.0, 2));
                    double maxDistance = Math.sqrt(Math.pow(width/2.0, 2) + Math.pow(length/2.0, 2));
                    double probability = 1.0 - (distance / maxDistance);
                    
                    if (random.nextDouble() < probability * 0.7) {
                        Location loc = baseLocation.clone().add(x, y, z);
                        placements.add(new BlockPlacement(loc, material));
                    }
                }
            }
        }
    }
    
    private void generateFoundation(List<BlockPlacement> placements, Location baseLocation,
                                   int width, int length, Material material) {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                Location loc = baseLocation.clone().add(x, -1, z);
                placements.add(new BlockPlacement(loc, material));
            }
        }
    }
    
    private void generateWalls(List<BlockPlacement> placements, Location baseLocation,
                              int width, int length, int height, Material material) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < length; z++) {
                    if (x == 0 || x == width - 1 || z == 0 || z == length - 1) {
                        Location loc = baseLocation.clone().add(x, y, z);
                        placements.add(new BlockPlacement(loc, material));
                    }
                }
            }
        }
    }
    
    private void generateRoof(List<BlockPlacement> placements, Location baseLocation,
                             int width, int length, int height, Material material) {
        // Simple peaked roof
        int roofHeight = Math.max(2, width / 4);
        
        for (int layer = 0; layer < roofHeight; layer++) {
            int roofWidth = width - (layer * 2);
            int roofLength = length - (layer * 2);
            
            if (roofWidth <= 0 || roofLength <= 0) break;
            
            int offset = layer;
            for (int x = 0; x < roofWidth; x++) {
                for (int z = 0; z < roofLength; z++) {
                    Location loc = baseLocation.clone().add(x + offset, height + layer, z + offset);
                    placements.add(new BlockPlacement(loc, material));
                }
            }
        }
    }
    
    private void generateFloor(List<BlockPlacement> placements, Location baseLocation,
                              int width, int length, int yOffset, Material material) {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                Location loc = baseLocation.clone().add(x, yOffset, z);
                placements.add(new BlockPlacement(loc, material));
            }
        }
    }
    
    private void generateSolidFill(List<BlockPlacement> placements, Location baseLocation,
                                  int width, int length, int height, Material material) {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                for (int y = 0; y < height; y++) {
                    Location loc = baseLocation.clone().add(x, y, z);
                    placements.add(new BlockPlacement(loc, material));
                }
            }
        }
    }
    
    private void generateDecorativeElement(List<BlockPlacement> placements, Location baseLocation,
                                          BuildRecipe.DecorativeElement element) {
        Material material = parseMaterial(element.getMaterial());
        if (material == null) return;
        
        switch (element.getType().toUpperCase()) {
            case "WINDOWS":
                generateWindows(placements, baseLocation, material);
                break;
            case "DOORS":
                generateDoors(placements, baseLocation, material);
                break;
            case "GARDEN":
                generateGarden(placements, baseLocation, material);
                break;
            case "LIGHTING":
                generateLighting(placements, baseLocation, material);
                break;
            case "FURNITURE":
                generateFurniture(placements, baseLocation, material);
                break;
        }
    }
    
    private void generateWindows(List<BlockPlacement> placements, Location baseLocation, Material material) {
        int width = recipe.getDimensions().getWidth();
        int length = recipe.getDimensions().getLength();
        int height = recipe.getDimensions().getHeight();
        
        // Add windows on walls at mid-height
        int windowHeight = height / 2;
        
        // Front and back walls
        for (int x = 2; x < width - 2; x += 3) {
            placements.add(new BlockPlacement(baseLocation.clone().add(x, windowHeight, 0), material));
            placements.add(new BlockPlacement(baseLocation.clone().add(x, windowHeight, length - 1), material));
        }
        
        // Side walls
        for (int z = 2; z < length - 2; z += 3) {
            placements.add(new BlockPlacement(baseLocation.clone().add(0, windowHeight, z), material));
            placements.add(new BlockPlacement(baseLocation.clone().add(width - 1, windowHeight, z), material));
        }
    }
    
    private void generateDoors(List<BlockPlacement> placements, Location baseLocation, Material material) {
        // Add door opening in front wall
        int doorZ = recipe.getDimensions().getLength() / 2;
        placements.add(new BlockPlacement(baseLocation.clone().add(0, 0, doorZ), Material.AIR));
        placements.add(new BlockPlacement(baseLocation.clone().add(0, 1, doorZ), Material.AIR));
        
        // Place actual door
        if (material == Material.GLASS) {
            material = Material.OAK_DOOR; // Default to wood door if glass specified
        }
        placements.add(new BlockPlacement(baseLocation.clone().add(0, 0, doorZ), material));
    }
    
    private void generateGarden(List<BlockPlacement> placements, Location baseLocation, Material material) {
        int width = recipe.getDimensions().getWidth();
        int length = recipe.getDimensions().getLength();
        
        // Create garden area around the structure
        for (int x = -2; x < width + 2; x++) {
            for (int z = -2; z < length + 2; z++) {
                if (x < 0 || x >= width || z < 0 || z >= length) {
                    if (Math.random() > 0.3) {
                        placements.add(new BlockPlacement(baseLocation.clone().add(x, 0, z), Material.GRASS_BLOCK));
                        if (Math.random() > 0.7) {
                            Material flower = Math.random() > 0.5 ? Material.POPPY : Material.DANDELION;
                            placements.add(new BlockPlacement(baseLocation.clone().add(x, 1, z), flower));
                        }
                    }
                }
            }
        }
    }
    
    private void generateLighting(List<BlockPlacement> placements, Location baseLocation, Material material) {
        int width = recipe.getDimensions().getWidth();
        int length = recipe.getDimensions().getLength();
        int height = recipe.getDimensions().getHeight();
        
        // Add torches or lanterns at corners and along walls
        Material lightSource = material == Material.GLOWSTONE ? Material.GLOWSTONE : Material.TORCH;
        
        // Corner lighting
        placements.add(new BlockPlacement(baseLocation.clone().add(1, height - 1, 1), lightSource));
        placements.add(new BlockPlacement(baseLocation.clone().add(width - 2, height - 1, 1), lightSource));
        placements.add(new BlockPlacement(baseLocation.clone().add(1, height - 1, length - 2), lightSource));
        placements.add(new BlockPlacement(baseLocation.clone().add(width - 2, height - 1, length - 2), lightSource));
    }
    
    private void generateFurniture(List<BlockPlacement> placements, Location baseLocation, Material material) {
        // Add basic furniture inside the structure
        int width = recipe.getDimensions().getWidth();
        int length = recipe.getDimensions().getLength();
        
        // Add some basic furniture blocks
        if (width > 4 && length > 4) {
            // Table
            placements.add(new BlockPlacement(baseLocation.clone().add(width/2, 1, length/2), Material.OAK_PLANKS));
            
            // Chairs
            placements.add(new BlockPlacement(baseLocation.clone().add(width/2 + 1, 1, length/2), Material.OAK_STAIRS));
            placements.add(new BlockPlacement(baseLocation.clone().add(width/2 - 1, 1, length/2), Material.OAK_STAIRS));
        }
    }
    
    private Material parseMaterial(String materialName) {
        try {
            return Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Try common aliases
            switch (materialName.toLowerCase()) {
                case "wood": return Material.OAK_PLANKS;
                case "stone": return Material.STONE;
                case "brick": return Material.BRICKS;
                case "glass": return Material.GLASS;
                case "iron": return Material.IRON_BLOCK;
                case "gold": return Material.GOLD_BLOCK;
                default: return Material.STONE; // Default fallback
            }
        }
    }
    
    @Override
    public int getEstimatedBlockCount(BuildRequest request) {
        if (recipe.getDimensions() != null) {
            int width = recipe.getDimensions().getWidth();
            int length = recipe.getDimensions().getLength();
            int height = recipe.getDimensions().getHeight();
            
            // Estimate based on structure complexity
            int baseBlocks = width * length * height;
            
            // Adjust for hollow structures
            if (recipe.getComponents() != null) {
                boolean hasHollow = recipe.getComponents().stream()
                    .anyMatch(c -> "HOLLOW".equalsIgnoreCase(c.getPattern()));
                if (hasHollow) {
                    baseBlocks = baseBlocks / 3; // Rough estimate for hollow structures
                }
            }
            
            return baseBlocks;
        }
        
        return request.getSize() * request.getSize() * 3; // Default estimation
    }
} 