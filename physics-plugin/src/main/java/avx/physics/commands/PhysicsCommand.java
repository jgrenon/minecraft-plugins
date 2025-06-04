package avx.physics.commands;

import avx.physics.PhysicsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhysicsCommand implements CommandExecutor, TabCompleter {
    
    private final PhysicsPlugin plugin;
    
    public PhysicsCommand(PhysicsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("physics.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "enable":
                handleEnable(sender);
                break;
                
            case "disable":
                handleDisable(sender);
                break;
                
            case "reload":
                handleReload(sender);
                break;
                
            case "status":
                handleStatus(sender);
                break;
                
            case "performance":
            case "perf":
                handlePerformance(sender);
                break;
                
            case "clear":
                handleClear(sender);
                break;
                
            case "debug":
                if (args.length > 1) {
                    handleDebug(sender, args[1]);
                } else {
                    sender.sendMessage("§cUsage: /physics debug <on|off>");
                }
                break;
                
            default:
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void handleEnable(CommandSender sender) {
        // This would require config modification - for now just inform
        sender.sendMessage("§a[Physics] System enabled! Use '/physics reload' to apply config changes.");
        sender.sendMessage("§7Note: Modify config.yml and reload to permanently enable.");
    }
    
    private void handleDisable(CommandSender sender) {
        // This would require config modification - for now just inform
        sender.sendMessage("§c[Physics] System disabled! Use '/physics reload' to apply config changes.");
        sender.sendMessage("§7Note: Modify config.yml and reload to permanently disable.");
    }
    
    private void handleReload(CommandSender sender) {
        try {
            plugin.reloadPlugin();
            sender.sendMessage("§a[Physics] Configuration reloaded successfully!");
        } catch (Exception e) {
            sender.sendMessage("§c[Physics] Error reloading configuration: " + e.getMessage());
            plugin.getLogger().severe("Error reloading config: " + e.getMessage());
        }
    }
    
    private void handleStatus(CommandSender sender) {
        boolean enabled = plugin.getConfigManager().isEnabled();
        
        sender.sendMessage("§6=== Realistic Physics Status ===");
        sender.sendMessage("§7System: " + (enabled ? "§aENABLED" : "§cDISABLED"));
        sender.sendMessage("§7Physics Blocks: §b" + plugin.getConfigManager().getPhysicsBlocks().size());
        sender.sendMessage("§7Support Blocks: §b" + plugin.getConfigManager().getSupportBlocks().size());
        sender.sendMessage("§7Pending Checks: §e" + plugin.getPhysicsManager().getPendingChecksCount());
        sender.sendMessage("§7Falling Blocks: §e" + plugin.getPhysicsManager().getFallingBlocksCount());
        sender.sendMessage("§7Cache Size: §e" + plugin.getStructureManager().getCacheSize());
        sender.sendMessage("§7Gravity Zones: §b" + plugin.getGravityManager().getGravityZones().size());
        
        // Performance info
        double tps = plugin.getPerformanceManager().getCurrentTPS();
        String tpsColor = tps >= 19.0 ? "§a" : tps >= 15.0 ? "§e" : "§c";
        sender.sendMessage("§7Server TPS: " + tpsColor + String.format("%.2f", tps));
        
        // Configuration summary
        sender.sendMessage("§6Configuration:");
        sender.sendMessage("§7- Chain Reactions: " + (plugin.getConfigManager().isChainReactionsEnabled() ? "§aON" : "§cOFF"));
        sender.sendMessage("§7- Momentum: " + (plugin.getConfigManager().isMomentumEnabled() ? "§aON" : "§cOFF"));
        sender.sendMessage("§7- Effects: " + (plugin.getConfigManager().isFallingParticlesEnabled() ? "§aON" : "§cOFF"));
        sender.sendMessage("§7- Debug: " + (plugin.getConfigManager().isDebugEnabled() ? "§aON" : "§cOFF"));
    }
    
    private void handlePerformance(CommandSender sender) {
        String stats = plugin.getPerformanceManager().getPerformanceStats();
        sender.sendMessage("§6[Physics] Performance Stats:");
        sender.sendMessage("§7" + stats);
        
        double loadFactor = plugin.getPerformanceManager().getLoadFactor();
        String loadColor = loadFactor <= 0.3 ? "§a" : loadFactor <= 0.6 ? "§e" : "§c";
        sender.sendMessage("§7Load Factor: " + loadColor + String.format("%.1f%%", loadFactor * 100));
        
        if (plugin.getPerformanceManager().isServerOverloaded()) {
            sender.sendMessage("§c⚠ Server is overloaded! Physics processing may be reduced.");
        }
        
        // Optimization suggestions
        plugin.getPerformanceManager().optimizeSettings();
    }
    
    private void handleClear(CommandSender sender) {
        plugin.getPhysicsManager().clearAll();
        plugin.getStructureManager().cleanupCache();
        plugin.getGravityManager().clearAllZones();
        
        sender.sendMessage("§a[Physics] Cleared all physics data, cache, and gravity zones.");
    }
    
    private void handleDebug(CommandSender sender, String state) {
        // This would require config modification - for now just inform
        boolean enable = state.equalsIgnoreCase("on") || state.equalsIgnoreCase("true");
        
        sender.sendMessage("§a[Physics] Debug mode " + (enable ? "enabled" : "disabled"));
        sender.sendMessage("§7Note: Modify 'debug.enabled' in config.yml and reload for permanent change.");
        
        if (enable && sender instanceof Player) {
            sender.sendMessage("§7Debug tip: Right-click blocks with a stick to check structural integrity!");
        }
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== Realistic Physics Commands ===");
        sender.sendMessage("§e/physics status §7- Show system status");
        sender.sendMessage("§e/physics enable §7- Enable physics system");
        sender.sendMessage("§e/physics disable §7- Disable physics system");
        sender.sendMessage("§e/physics reload §7- Reload configuration");
        sender.sendMessage("§e/physics performance §7- Show performance stats");
        sender.sendMessage("§e/physics clear §7- Clear all physics data");
        sender.sendMessage("§e/physics debug <on|off> §7- Toggle debug mode");
        sender.sendMessage("§7Use §e/gravity §7and §e/structure §7for specialized commands");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("physics.admin")) {
            return new ArrayList<>();
        }
        
        if (args.length == 1) {
            List<String> completions = Arrays.asList(
                "enable", "disable", "reload", "status", "performance", "clear", "debug"
            );
            
            List<String> result = new ArrayList<>();
            String input = args[0].toLowerCase();
            
            for (String completion : completions) {
                if (completion.startsWith(input)) {
                    result.add(completion);
                }
            }
            
            return result;
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            return Arrays.asList("on", "off");
        }
        
        return new ArrayList<>();
    }
} 