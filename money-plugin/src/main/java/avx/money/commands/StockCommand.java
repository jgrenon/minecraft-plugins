package avx.money.commands;

import avx.money.MoneyPlugin;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

public class StockCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        int total = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (item.getType() == Material.EMERALD) {
                total += item.getAmount();
                player.getInventory().remove(item);
            } else if (item.getType() == Material.EMERALD_BLOCK) {
                total += item.getAmount() * 9;
                player.getInventory().remove(item);
            }
        }

        MoneyPlugin.getInstance().balanceManager.addBalance(player, total);
        player.sendMessage("§aVous avez " + total + "$ de plus.");
        return true;
    }
}