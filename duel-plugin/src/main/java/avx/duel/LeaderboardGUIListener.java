package avx.duel;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class LeaderboardGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if the inventory title matches our leaderboard GUI
        Component expectedTitle = Component.text("Duel Leaderboard").color(NamedTextColor.GOLD);
        String currentTitle = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        String expectedTitleText = PlainTextComponentSerializer.plainText().serialize(expectedTitle);
        
        if (currentTitle.equals(expectedTitleText)) {
            event.setCancelled(true); // prevent item movement
        }
    }
}