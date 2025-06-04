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

public class GravityCommand implements CommandExecutor, TabCompleter {
    
    private final PhysicsPlugin plugin;
    
    public GravityCommand(PhysicsPlugin plugin) {
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
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        try {
            double strength = Double.parseDouble(args[0]);
            double radius = args.length > 1 ? Double.parseDouble(args[1]) : 10.0;
            
            Location location = player.getLocation();
            plugin.getGravityManager().setGravityZone(location, radius, strength);
            
            player.sendMessage("§a[Gravity] Created gravity zone:");
            player.sendMessage("§7- Location: " + formatLocation(location));
            player.sendMessage("§7- Strength: §b" + strength + "x");
            player.sendMessage("§7- Radius: §b" + radius + " blocks");
            
            // Give feedback about the gravity effect
            if (strength > 1.0) {
                player.sendMessage("§e⚠ Stronger gravity - blocks will fall faster!");
            } else if (strength < 1.0 && strength > 0) {
                player.sendMessage("§a✓ Weaker gravity - blocks will fall slower!");
            } else if (strength <= 0) {
                player.sendMessage("§d✨ Anti-gravity - blocks will float upward!");
            }
            
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid number format! Use: /gravity <strength> [radius]");
            player.sendMessage("§7Examples:");
            player.sendMessage("§7- /gravity 2.0 §8(double gravity)");
            player.sendMessage("§7- /gravity 0.5 15 §8(half gravity, 15 block radius)");
            player.sendMessage("§7- /gravity -0.5 §8(anti-gravity)");
        }
        
        return true;
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§6=== Gravity Zone Commands ===");
        player.sendMessage("§e/gravity <strength> [radius] §7- Create gravity zone");
        player.sendMessage("§7");
        player.sendMessage("§7Strength examples:");
        player.sendMessage("§7- §b1.0 §7= Normal gravity");
        player.sendMessage("§7- §b2.0 §7= Double gravity (faster falling)");
        player.sendMessage("§7- §b0.5 §7= Half gravity (slower falling)");
        player.sendMessage("§7- §b0.0 §7= No gravity (floating)");
        player.sendMessage("§7- §b-1.0 §7= Anti-gravity (upward)");
        player.sendMessage("§7");
        player.sendMessage("§7Default radius: 10 blocks");
        
        // Show current gravity at player location
        double currentGravity = plugin.getGravityManager().getGravityStrength(player.getLocation());
        player.sendMessage("§7Current gravity here: §b" + currentGravity + "x");
        
        // Show nearby gravity zones
        int zoneCount = plugin.getGravityManager().getGravityZones().size();
        if (zoneCount > 0) {
            player.sendMessage("§7Total gravity zones: §b" + zoneCount);
        }
    }
    
    private String formatLocation(Location loc) {
        return String.format("%.0f, %.0f, %.0f in %s", 
            loc.getX(), loc.getY(), loc.getZ(), loc.getWorld().getName());
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("physics.admin")) {
            return new ArrayList<>();
        }
        
        if (args.length == 1) {
            // Suggest common gravity values
            return Arrays.asList("0.0", "0.5", "1.0", "1.5", "2.0", "-0.5", "-1.0");
        }
        
        if (args.length == 2) {
            // Suggest common radius values
            return Arrays.asList("5", "10", "15", "20", "25");
        }
        
        return new ArrayList<>();
    }
} 