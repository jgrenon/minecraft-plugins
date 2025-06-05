package avx.aibuilder.managers;

import avx.aibuilder.AIBuilderPlugin;
import avx.aibuilder.ai.OpenAIService;
import avx.aibuilder.data.BuildRecipe;
import avx.aibuilder.data.BuildRequest;
import avx.aibuilder.data.StructureType;
import avx.aibuilder.data.BuildingMaterial;
import avx.aibuilder.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class AIManager {
    
    private final AIBuilderPlugin plugin;
    private final OpenAIService openAIService;
    private final Map<String, StructureType> structureKeywords;
    private final Map<String, BuildingMaterial> materialKeywords;
    private final Map<String, Integer> sizeKeywords;
    private final List<String> positiveModifiers;
    private final List<String> styleKeywords;
    
    public AIManager(AIBuilderPlugin plugin) {
        this.plugin = plugin;
        this.openAIService = new OpenAIService(plugin);
        this.structureKeywords = new HashMap<>();
        this.materialKeywords = new HashMap<>();
        this.sizeKeywords = new HashMap<>();
        this.positiveModifiers = new ArrayList<>();
        this.styleKeywords = new ArrayList<>();
        
        initializeKeywords();
    }
    
    private void initializeKeywords() {
        // Structure types
        structureKeywords.put("house", StructureType.HOUSE);
        structureKeywords.put("home", StructureType.HOUSE);
        structureKeywords.put("building", StructureType.HOUSE);
        structureKeywords.put("castle", StructureType.CASTLE);
        structureKeywords.put("fortress", StructureType.CASTLE);
        structureKeywords.put("tower", StructureType.TOWER);
        structureKeywords.put("spire", StructureType.TOWER);
        structureKeywords.put("bridge", StructureType.BRIDGE);
        structureKeywords.put("wall", StructureType.WALL);
        structureKeywords.put("fence", StructureType.WALL);
        structureKeywords.put("pyramid", StructureType.PYRAMID);
        structureKeywords.put("dome", StructureType.DOME);
        structureKeywords.put("sphere", StructureType.DOME);
        structureKeywords.put("tree", StructureType.TREE);
        structureKeywords.put("garden", StructureType.GARDEN);
        structureKeywords.put("park", StructureType.GARDEN);
        structureKeywords.put("road", StructureType.ROAD);
        structureKeywords.put("path", StructureType.ROAD);
        
        // Materials
        materialKeywords.put("stone", new BuildingMaterial(Material.STONE, "stone"));
        materialKeywords.put("wood", new BuildingMaterial(Material.OAK_PLANKS, "wood"));
        materialKeywords.put("wooden", new BuildingMaterial(Material.OAK_PLANKS, "wood"));
        materialKeywords.put("brick", new BuildingMaterial(Material.BRICKS, "brick"));
        materialKeywords.put("cobblestone", new BuildingMaterial(Material.COBBLESTONE, "cobblestone"));
        materialKeywords.put("cobble", new BuildingMaterial(Material.COBBLESTONE, "cobblestone"));
        materialKeywords.put("glass", new BuildingMaterial(Material.GLASS, "glass"));
        materialKeywords.put("iron", new BuildingMaterial(Material.IRON_BLOCK, "iron"));
        materialKeywords.put("gold", new BuildingMaterial(Material.GOLD_BLOCK, "gold"));
        materialKeywords.put("diamond", new BuildingMaterial(Material.DIAMOND_BLOCK, "diamond"));
        materialKeywords.put("quartz", new BuildingMaterial(Material.QUARTZ_BLOCK, "quartz"));
        materialKeywords.put("sandstone", new BuildingMaterial(Material.SANDSTONE, "sandstone"));
        materialKeywords.put("oak", new BuildingMaterial(Material.OAK_PLANKS, "oak"));
        materialKeywords.put("spruce", new BuildingMaterial(Material.SPRUCE_PLANKS, "spruce"));
        materialKeywords.put("birch", new BuildingMaterial(Material.BIRCH_PLANKS, "birch"));
        
        // Sizes
        sizeKeywords.put("tiny", 3);
        sizeKeywords.put("small", 5);
        sizeKeywords.put("medium", 10);
        sizeKeywords.put("big", 15);
        sizeKeywords.put("large", 20);
        sizeKeywords.put("huge", 30);
        sizeKeywords.put("massive", 50);
        sizeKeywords.put("gigantic", 75);
        
        // Positive modifiers
        positiveModifiers.addAll(Arrays.asList(
            "beautiful", "nice", "cool", "awesome", "amazing", "stunning", 
            "elegant", "magnificent", "gorgeous", "fantastic", "wonderful"
        ));
        
        // Style keywords
        styleKeywords.addAll(Arrays.asList(
            "medieval", "modern", "rustic", "fantasy", "futuristic", 
            "classical", "gothic", "baroque", "minimalist", "ornate"
        ));
    }
    
    public CompletableFuture<BuildRequest> interpretPromptAsync(String prompt, Player player) {
        // Try OpenAI first if configured
        if (openAIService.isConfigured()) {
            return openAIService.generateBuildRecipe(prompt)
                .thenApply(recipe -> convertRecipeToRequest(recipe, player, prompt))
                .exceptionally(throwable -> {
                    plugin.getLogger().warning("OpenAI failed, falling back to keyword interpretation: " + throwable.getMessage());
                    MessageUtils.sendWarning(player, "AI service unavailable, using basic interpretation...");
                    return interpretPromptFallback(prompt, player);
                });
        } else {
            // Use fallback keyword-based interpretation
            return CompletableFuture.completedFuture(interpretPromptFallback(prompt, player));
        }
    }
    
    public BuildRequest interpretPrompt(String prompt, Player player) {
        return interpretPromptFallback(prompt, player);
    }
    
    private BuildRequest interpretPromptFallback(String prompt, Player player) {
        String lowerPrompt = prompt.toLowerCase().trim();
        
        BuildRequest request = new BuildRequest();
        request.setPlayer(player);
        request.setOriginalPrompt(prompt);
        
        // Detect structure type
        StructureType structureType = detectStructureType(lowerPrompt);
        request.setStructureType(structureType);
        
        // Detect material
        BuildingMaterial material = detectMaterial(lowerPrompt);
        if (material != null) {
            request.setMaterial(material);
        } else {
            request.setMaterial(getDefaultMaterial(structureType));
        }
        
        // Detect size
        int size = detectSize(lowerPrompt);
        request.setSize(size);
        
        // Detect style
        String style = detectStyle(lowerPrompt);
        request.setStyle(style);
        
        // Set build location to player's location
        request.setLocation(player.getLocation());
        
        // Calculate complexity score
        request.setComplexity(calculateComplexity(request));
        
        return request;
    }
    
    private BuildRequest convertRecipeToRequest(BuildRecipe recipe, Player player, String originalPrompt) {
        BuildRequest request = new BuildRequest();
        request.setPlayer(player);
        request.setOriginalPrompt(originalPrompt);
        request.setLocation(player.getLocation());
        
        // Convert recipe to request format
        try {
            StructureType structureType = StructureType.valueOf(recipe.getStructureType().toUpperCase());
            request.setStructureType(structureType);
        } catch (IllegalArgumentException e) {
            request.setStructureType(StructureType.HOUSE); // Default fallback
        }
        
        // Set size based on recipe dimensions
        if (recipe.getDimensions() != null) {
            int maxDimension = Math.max(recipe.getDimensions().getWidth(), 
                                      Math.max(recipe.getDimensions().getLength(), 
                                             recipe.getDimensions().getHeight()));
            request.setSize(maxDimension);
        } else {
            request.setSize(8);
        }
        
        // Set material
        Material material = parseMaterial(recipe.getPrimaryMaterial());
        String materialName = recipe.getPrimaryMaterial().toLowerCase();
        request.setMaterial(new BuildingMaterial(material, materialName));
        
        // Set style
        request.setStyle(recipe.getStyle() != null ? recipe.getStyle() : "default");
        
        // Set complexity from recipe
        request.setComplexity(recipe.getComplexity());
        
        // Store the recipe for advanced generation
        request.setBuildRecipe(recipe);
        
        return request;
    }
    
    private Material parseMaterial(String materialName) {
        if (materialName == null) return Material.OAK_PLANKS;
        
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
                default: return Material.OAK_PLANKS;
            }
        }
    }
    
    private StructureType detectStructureType(String prompt) {
        for (Map.Entry<String, StructureType> entry : structureKeywords.entrySet()) {
            if (prompt.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return StructureType.HOUSE; // Default
    }
    
    private BuildingMaterial detectMaterial(String prompt) {
        for (Map.Entry<String, BuildingMaterial> entry : materialKeywords.entrySet()) {
            if (prompt.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    private int detectSize(String prompt) {
        // Check for specific numbers first
        Pattern numberPattern = Pattern.compile("(\\d+)\\s*(?:x|by|×)\\s*(\\d+)");
        java.util.regex.Matcher matcher = numberPattern.matcher(prompt);
        if (matcher.find()) {
            int width = Integer.parseInt(matcher.group(1));
            int length = Integer.parseInt(matcher.group(2));
            return Math.max(width, length);
        }
        
        // Check for size keywords
        for (Map.Entry<String, Integer> entry : sizeKeywords.entrySet()) {
            if (prompt.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        return 8; // Default medium size
    }
    
    private String detectStyle(String prompt) {
        for (String style : styleKeywords) {
            if (prompt.contains(style)) {
                return style;
            }
        }
        return "default";
    }
    
    private BuildingMaterial getDefaultMaterial(StructureType type) {
        switch (type) {
            case CASTLE:
                return new BuildingMaterial(Material.STONE_BRICKS, "stone_brick");
            case TOWER:
                return new BuildingMaterial(Material.COBBLESTONE, "cobblestone");
            case BRIDGE:
                return new BuildingMaterial(Material.OAK_PLANKS, "oak");
            case WALL:
                return new BuildingMaterial(Material.COBBLESTONE, "cobblestone");
            case PYRAMID:
                return new BuildingMaterial(Material.SANDSTONE, "sandstone");
            case DOME:
                return new BuildingMaterial(Material.QUARTZ_BLOCK, "quartz");
            case TREE:
                return new BuildingMaterial(Material.OAK_LOG, "oak_log");
            case GARDEN:
                return new BuildingMaterial(Material.GRASS_BLOCK, "grass");
            case ROAD:
                return new BuildingMaterial(Material.STONE, "stone");
            default:
                return new BuildingMaterial(Material.OAK_PLANKS, "oak");
        }
    }
    
    private int calculateComplexity(BuildRequest request) {
        int complexity = 1;
        
        // Base complexity by structure type
        switch (request.getStructureType()) {
            case HOUSE:
                complexity += 2;
                break;
            case CASTLE:
                complexity += 5;
                break;
            case TOWER:
                complexity += 3;
                break;
            case BRIDGE:
                complexity += 4;
                break;
            case PYRAMID:
                complexity += 6;
                break;
            case DOME:
                complexity += 7;
                break;
            default:
                complexity += 1;
        }
        
        // Size multiplier
        complexity += request.getSize() / 5;
        
        // Style modifier
        if (!"default".equals(request.getStyle())) {
            complexity += 2;
        }
        
        return Math.max(1, complexity);
    }
    
    public String generateBuildDescription(BuildRequest request) {
        StringBuilder description = new StringBuilder();
        
        description.append("Building a ");
        
        if (request.getSize() > 20) {
            description.append("large ");
        } else if (request.getSize() < 8) {
            description.append("small ");
        }
        
        if (!"default".equals(request.getStyle())) {
            description.append(request.getStyle()).append(" ");
        }
        
        description.append(request.getStructureType().name().toLowerCase());
        description.append(" made of ").append(request.getMaterial().getName());
        description.append(" (").append(request.getSize()).append("x").append(request.getSize()).append(")");
        
        return description.toString();
    }
} 