package avx.money.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ShareCommand implements CommandExecutor {

    private final Inventory sharedInventory = Bukkit.createInventory(null, 27, "§bCompte partagé");

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            player.openInventory(sharedInventory);
            return true;
        }
        return false;
    }
}