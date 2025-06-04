package avx.physics.listeners;

import avx.physics.PhysicsPlugin;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerInteractionListener implements Listener {
    
    private final PhysicsPlugin plugin;
    
    public PlayerInteractionListener(PhysicsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        // Check if player should bypass physics
        if (shouldBypassPhysics(player)) {
            // Don't trigger physics for creative mode players with bypass permission
            return;
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        // Notify players about physics if debug is enabled
        if (plugin.getConfigManager().isDebugEnabled() && 
            player.hasPermission("physics.notify")) {
            
            if (plugin.getConfigManager().getPhysicsBlocks().contains(event.getBlock().getType())) {
                player.sendMessage("§7[Physics] §aBlock has realistic physics enabled!");
            }
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Send welcome message to admins
        if (player.hasPermission("physics.admin")) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                player.sendMessage("§6[Realistic Physics] §aPlugin is " + 
                    (plugin.getConfigManager().isEnabled() ? "§2ENABLED" : "§cDISABLED"));
                player.sendMessage("§7Use /physics for commands");
            }, 40L); // 2 second delay
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        // Debug feature: right-click with stick to check structural integrity
        if (plugin.getConfigManager().isDebugEnabled() && 
            player.hasPermission("physics.admin") &&
            event.hasItem() && 
            event.getItem().getType().name().contains("STICK") &&
            event.hasBlock()) {
            
            double integrity = plugin.getStructureManager().getStructuralIntegrity(event.getClickedBlock().getLocation());
            boolean hasSupport = plugin.getStructureManager().hasStructuralSupport(event.getClickedBlock().getLocation());
            
            player.sendMessage("§6[Physics Debug] §7Block: " + event.getClickedBlock().getType());
            player.sendMessage("§7Structural Integrity: §" + getIntegrityColor(integrity) + String.format("%.1f%%", integrity));
            player.sendMessage("§7Has Support: " + (hasSupport ? "§aYes" : "§cNo"));
            
            event.setCancelled(true);
        }
    }
    
    private boolean shouldBypassPhysics(Player player) {
        return player.getGameMode() == GameMode.CREATIVE && 
               player.hasPermission("physics.bypass");
    }
    
    private String getIntegrityColor(double integrity) {
        if (integrity >= 75) return "a"; // Green
        if (integrity >= 50) return "e"; // Yellow
        if (integrity >= 25) return "6"; // Gold
        return "c"; // Red
    }
} 