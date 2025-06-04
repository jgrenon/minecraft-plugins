package avx.physics.commands;

import avx.physics.PhysicsPlugin;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StructureCommand implements CommandExecutor, TabCompleter {
    
    private final PhysicsPlugin plugin;
    
    public StructureCommand(PhysicsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("physics.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        int radius = 5; // Default radius
        
        if (args.length > 0) {
            try {
                radius = Integer.parseInt(args[0]);
                if (radius < 1 || radius > 20) {
                    player.sendMessage("§cRadius must be between 1 and 20!");
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§cInvalid radius! Use a number between 1 and 20.");
                return true;
            }
        }
        
        Location center = player.getLocation();
        player.sendMessage("§6[Structure] Analyzing structural integrity...");
        player.sendMessage("§7Center: " + formatLocation(center));
        player.sendMessage("§7Radius: §b" + radius + " blocks");
        
        // Analyze the area
        Map<Location, Double> analysis = plugin.getStructureManager().analyzeArea(center, radius);
        
        if (analysis.isEmpty()) {
            player.sendMessage("§7No physics-enabled blocks found in this area.");
            return true;
        }
        
        // Calculate statistics
        double totalIntegrity = 0;
        int stableBlocks = 0;
        int unstableBlocks = 0;
        int criticalBlocks = 0;
        
        for (double integrity : analysis.values()) {
            totalIntegrity += integrity;
            
            if (integrity >= 75) {
                stableBlocks++;
            } else if (integrity >= 25) {
                unstableBlocks++;
            } else {
                criticalBlocks++;
            }
        }
        
        double averageIntegrity = totalIntegrity / analysis.size();
        
        // Display results
        player.sendMessage("§6=== Structural Analysis Results ===");
        player.sendMessage("§7Total blocks analyzed: §b" + analysis.size());
        player.sendMessage("§7Average integrity: §" + getIntegrityColor(averageIntegrity) + 
                          String.format("%.1f%%", averageIntegrity));
        
        player.sendMessage("§7Block stability:");
        player.sendMessage("§a  Stable (75%+): §f" + stableBlocks);
        player.sendMessage("§e  Unstable (25-75%): §f" + unstableBlocks);
        player.sendMessage("§c  Critical (<25%): §f" + criticalBlocks);
        
        // Warning for critical structures
        if (criticalBlocks > 0) {
            player.sendMessage("§c⚠ Warning: " + criticalBlocks + " blocks may collapse!");
        }
        
        // Show integrity at player's location
        double playerLocationIntegrity = plugin.getStructureManager().getStructuralIntegrity(center);
        boolean hasSupport = plugin.getStructureManager().hasStructuralSupport(center);
        
        player.sendMessage("§7");
        player.sendMessage("§7Your location integrity: §" + getIntegrityColor(playerLocationIntegrity) + 
                          String.format("%.1f%%", playerLocationIntegrity));
        player.sendMessage("§7Has structural support: " + (hasSupport ? "§aYes" : "§cNo"));
        
        // Performance info
        int cacheSize = plugin.getStructureManager().getCacheSize();
        player.sendMessage("§7Structure cache size: §b" + cacheSize + " entries");
        
        return true;
    }
    
    private String formatLocation(Location loc) {
        return String.format("%.0f, %.0f, %.0f", loc.getX(), loc.getY(), loc.getZ());
    }
    
    private String getIntegrityColor(double integrity) {
        if (integrity >= 75) return "a"; // Green
        if (integrity >= 50) return "e"; // Yellow
        if (integrity >= 25) return "6"; // Gold
        return "c"; // Red
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("physics.admin")) {
            return new ArrayList<>();
        }
        
        if (args.length == 1) {
            return Arrays.asList("3", "5", "10", "15", "20");
        }
        
        return new ArrayList<>();
    }
} 