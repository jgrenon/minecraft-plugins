package avx.duel;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Map;

public class DuelListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player loser = event.getEntity();
        DuelPlugin plugin = JavaPlugin.getPlugin(DuelPlugin.class);

        if (!plugin.ongoingDuels.containsKey(loser))
            return;

        Player winner = plugin.ongoingDuels.get(loser);
        if (winner == null) {
            // Try reverse lookup
            for (Map.Entry<Player, Player> entry : plugin.ongoingDuels.entrySet()) {
                if (entry.getValue().equals(loser)) {
                    winner = entry.getKey();
                    break;
                }
            }
        }

        if (winner == null) {
            // Clean up corrupted state
            plugin.ongoingDuels.remove(loser);
            return;
        }

        // Use the new clean ending method
        plugin.endDuelCleanly(loser, winner, "death");

        // Broadcast result (already handled in endDuelCleanly, but keeping for compatibility)
        Bukkit.broadcast(Component.text("[DUEL] ").color(NamedTextColor.GOLD)
                .append(Component.text(winner.getName()).color(NamedTextColor.YELLOW))
                .append(Component.text(" defeated ").color(NamedTextColor.WHITE))
                .append(Component.text(loser.getName()).color(NamedTextColor.YELLOW))
                .append(Component.text(" in a duel!").color(NamedTextColor.WHITE)));
    }
}