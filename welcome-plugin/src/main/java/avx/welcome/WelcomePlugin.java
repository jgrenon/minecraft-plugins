package avx.welcome;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class WelcomePlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Enregistre les événements
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("Plugin WelcomePlugin activé !");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin WelcomePlugin désactivé !");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String title = ChatColor.GOLD + "Bienvenue " + ChatColor.YELLOW + player.getName();
        player.sendTitle(title, "", 20, 60, 20); // fadeIn, stay, fadeOut (en ticks)
    }
}