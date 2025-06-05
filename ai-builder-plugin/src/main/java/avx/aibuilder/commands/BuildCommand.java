package avx.aibuilder.commands;

import avx.aibuilder.AIBuilderPlugin;
import avx.aibuilder.data.BuildRequest;
import avx.aibuilder.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuildCommand implements CommandExecutor {
    
    private final AIBuilderPlugin plugin;
    
    public BuildCommand(AIBuilderPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Check permission
        if (!player.hasPermission("aibuilder.build")) {
            MessageUtils.sendError(player, "You don't have permission to use the build command!");
            return true;
        }
        
        // Check if args are provided
        if (args.length == 0) {
            MessageUtils.sendError(player, "Usage: /build <description>");
            MessageUtils.sendInfo(player, "Examples:");
            MessageUtils.sendInfo(player, "  /build a large stone castle");
            MessageUtils.sendInfo(player, "  /build a small wooden house");
            MessageUtils.sendInfo(player, "  /build a 10x10 brick tower");
            MessageUtils.sendInfo(player, "  /build a medieval bridge");
            return true;
        }
        
        // Check if player already has an active build
        BuildRequest existingBuild = plugin.getBuildManager().getActiveBuild(player.getUniqueId());
        if (existingBuild != null) {
            MessageUtils.sendError(player, "You already have an active build in progress!");
            MessageUtils.sendInfo(player, "Use /ai cancel to cancel your current build.");
            return true;
        }
        
        // Combine all arguments into a single prompt
        StringBuilder promptBuilder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            promptBuilder.append(args[i]);
            if (i < args.length - 1) {
                promptBuilder.append(" ");
            }
        }
        String prompt = promptBuilder.toString();
        
        // Interpret the prompt using AI manager
        MessageUtils.sendInfo(player, "Interpreting your request with AI...");
        
        plugin.getAIManager().interpretPromptAsync(prompt, player)
            .thenAccept(buildRequest -> {
                try {
                    // Show interpretation to player for confirmation
                    String description;
                    if (buildRequest.hasAdvancedRecipe()) {
                        description = buildRequest.getBuildRecipe().getDescription();
                        MessageUtils.sendSuccess(player, "✨ OpenAI Interpretation: " + description);
                        MessageUtils.sendInfo(player, "Structure: " + buildRequest.getBuildRecipe().getName());
                        MessageUtils.sendInfo(player, "Dimensions: " + buildRequest.getBuildRecipe().getDimensions());
                        MessageUtils.sendInfo(player, "Style: " + buildRequest.getBuildRecipe().getStyle());
                        if (buildRequest.getBuildRecipe().getFeatures() != null && !buildRequest.getBuildRecipe().getFeatures().isEmpty()) {
                            MessageUtils.sendInfo(player, "Features: " + String.join(", ", buildRequest.getBuildRecipe().getFeatures()));
                        }
                    } else {
                        description = plugin.getAIManager().generateBuildDescription(buildRequest);
                        MessageUtils.sendSuccess(player, "🔧 Basic Interpretation: " + description);
                    }
                    
                    MessageUtils.sendInfo(player, "Estimated complexity: " + buildRequest.getComplexity() + "/10");
                    
                    // Start the build
                    plugin.getBuildManager().startBuild(buildRequest);
                    
                } catch (Exception e) {
                    MessageUtils.sendError(player, "Error starting build: " + e.getMessage());
                    plugin.getLogger().warning("Error starting build for " + player.getName() + ": " + e.getMessage());
                }
            })
            .exceptionally(throwable -> {
                MessageUtils.sendError(player, "Error interpreting your request: " + throwable.getMessage());
                plugin.getLogger().warning("Error processing build command for " + player.getName() + ": " + throwable.getMessage());
                return null;
            });
        
        return true;
    }
} 