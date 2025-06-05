package avx.aibuilder.commands;

import avx.aibuilder.AIBuilderPlugin;
import avx.aibuilder.data.BuildRequest;
import avx.aibuilder.data.StructureType;
import avx.aibuilder.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AICommand implements CommandExecutor {
    
    private final AIBuilderPlugin plugin;
    
    public AICommand(AIBuilderPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "help":
                sendHelp(sender);
                break;
                
            case "status":
                handleStatus(sender);
                break;
                
            case "reload":
                handleReload(sender);
                break;
                
            case "cancel":
                handleCancel(sender, args);
                break;
                
            case "list":
                handleList(sender);
                break;
                
            case "structures":
                handleStructures(sender);
                break;
                
            default:
                MessageUtils.sendError(sender, "Unknown subcommand: " + subCommand);
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        MessageUtils.sendInfo(sender, "=== AI Builder Commands ===");
        MessageUtils.sendInfo(sender, "/ai help - Show this help message");
        MessageUtils.sendInfo(sender, "/ai status - Show plugin status");
        MessageUtils.sendInfo(sender, "/ai list - List active builds");
        MessageUtils.sendInfo(sender, "/ai structures - List available structure types");
        MessageUtils.sendInfo(sender, "/ai cancel [player] - Cancel a build");
        
        if (sender.hasPermission("aibuilder.admin")) {
            MessageUtils.sendInfo(sender, "/ai reload - Reload plugin configuration");
        }
        
        MessageUtils.sendInfo(sender, "");
        MessageUtils.sendInfo(sender, "=== Build Examples ===");
        MessageUtils.sendInfo(sender, "/build a large stone castle");
        MessageUtils.sendInfo(sender, "/build a small wooden house");
        MessageUtils.sendInfo(sender, "/build a 15x15 brick tower");
        MessageUtils.sendInfo(sender, "/build a medieval bridge made of cobblestone");
        MessageUtils.sendInfo(sender, "/build a beautiful garden with flowers");
    }
    
    private void handleStatus(CommandSender sender) {
        List<BuildRequest> activeBuilds = plugin.getBuildManager().getActiveBuilds();
        
        MessageUtils.sendSuccess(sender, "=== AI Builder Status ===");
        MessageUtils.sendInfo(sender, "Plugin Version: " + plugin.getDescription().getVersion());
        MessageUtils.sendInfo(sender, "Active Builds: " + activeBuilds.size());
        MessageUtils.sendInfo(sender, "Available Structures: " + StructureType.values().length);
        
        // OpenAI status
        boolean openAIConfigured = !plugin.getConfigManager().getOpenAIApiKey().isEmpty();
        String aiStatus = openAIConfigured ? "✅ OpenAI Enabled" : "⚠️ Using Basic AI (OpenAI not configured)";
        MessageUtils.sendInfo(sender, "AI System: " + aiStatus);
        
        if (sender.hasPermission("aibuilder.admin")) {
            MessageUtils.sendInfo(sender, "Max Build Size: " + plugin.getConfigManager().getMaxBuildSize());
            MessageUtils.sendInfo(sender, "Build Cooldown: " + plugin.getConfigManager().getBuildCooldown() + "s");
            if (openAIConfigured) {
                MessageUtils.sendInfo(sender, "OpenAI Model: " + plugin.getConfigManager().getOpenAIModel());
            }
        }
    }
    
    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("aibuilder.admin")) {
            MessageUtils.sendError(sender, "You don't have permission to reload the plugin!");
            return;
        }
        
        try {
            plugin.getConfigManager().loadConfig();
            MessageUtils.sendSuccess(sender, "AI Builder configuration reloaded successfully!");
        } catch (Exception e) {
            MessageUtils.sendError(sender, "Error reloading configuration: " + e.getMessage());
        }
    }
    
    private void handleCancel(CommandSender sender, String[] args) {
        if (!(sender instanceof Player) && args.length < 2) {
            MessageUtils.sendError(sender, "Usage: /ai cancel [player]");
            return;
        }
        
        Player targetPlayer;
        if (args.length >= 2) {
            if (!sender.hasPermission("aibuilder.admin")) {
                MessageUtils.sendError(sender, "You don't have permission to cancel other players' builds!");
                return;
            }
            targetPlayer = plugin.getServer().getPlayer(args[1]);
            if (targetPlayer == null) {
                MessageUtils.sendError(sender, "Player not found: " + args[1]);
                return;
            }
        } else {
            targetPlayer = (Player) sender;
        }
        
        BuildRequest activeBuild = plugin.getBuildManager().getActiveBuild(targetPlayer.getUniqueId());
        if (activeBuild == null) {
            MessageUtils.sendError(sender, targetPlayer.getName() + " doesn't have any active builds.");
            return;
        }
        
        plugin.getBuildManager().cancelBuild(activeBuild.getId());
        MessageUtils.sendSuccess(sender, "Cancelled " + targetPlayer.getName() + "'s build.");
        
        if (!sender.equals(targetPlayer)) {
            MessageUtils.sendInfo(targetPlayer, "Your build was cancelled by " + sender.getName());
        }
    }
    
    private void handleList(CommandSender sender) {
        List<BuildRequest> activeBuilds = plugin.getBuildManager().getActiveBuilds();
        
        if (activeBuilds.isEmpty()) {
            MessageUtils.sendInfo(sender, "No active builds.");
            return;
        }
        
        MessageUtils.sendSuccess(sender, "=== Active Builds ===");
        for (BuildRequest build : activeBuilds) {
            String description = plugin.getAIManager().generateBuildDescription(build);
            MessageUtils.sendInfo(sender, build.getPlayer().getName() + ": " + description);
        }
    }
    
    private void handleStructures(CommandSender sender) {
        MessageUtils.sendSuccess(sender, "=== Available Structure Types ===");
        for (StructureType type : StructureType.values()) {
            MessageUtils.sendInfo(sender, "• " + type.getDisplayName() + " - " + type.getDescription());
        }
        
        MessageUtils.sendInfo(sender, "");
        MessageUtils.sendInfo(sender, "You can also specify:");
        MessageUtils.sendInfo(sender, "• Materials: stone, wood, brick, glass, iron, etc.");
        MessageUtils.sendInfo(sender, "• Sizes: tiny, small, medium, large, huge, or specific dimensions");
        MessageUtils.sendInfo(sender, "• Styles: medieval, modern, rustic, fantasy, etc.");
    }
} 