package avx.aibuilder.listeners;

import avx.aibuilder.AIBuilderPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    
    private final AIBuilderPlugin plugin;
    
    public PlayerListener(AIBuilderPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Future: Could show welcome message about AI Builder features
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Cancel any active builds for the disconnecting player
        // This prevents builds from continuing when players are offline
        var activeBuild = plugin.getBuildManager().getActiveBuild(event.getPlayer().getUniqueId());
        if (activeBuild != null) {
            plugin.getBuildManager().cancelBuild(activeBuild.getId());
        }
    }
} 