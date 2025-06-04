package avx.duel;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class DuelCommandFilter implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        DuelPlugin plugin = JavaPlugin.getPlugin(DuelPlugin.class);

        if (!plugin.ongoingDuels.containsKey(player) && !plugin.ongoingDuels.containsValue(player))
            return;

        String msg = event.getMessage().toLowerCase();
        if (msg.startsWith("/duel") && !msg.startsWith("/duel forfeit")) {
            event.setCancelled(true);
            player.sendMessage(Component.text("You may only use /duel forfeit during a duel.").color(NamedTextColor.RED));
        }
    }
}