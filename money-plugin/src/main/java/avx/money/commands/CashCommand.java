package avx.money.commands;

import avx.money.MoneyPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CashCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length != 2) {
            player.sendMessage("§cUsage: /cash <joueur> <montant>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage("§cJoueur introuvable.");
            return true;
        }

        try {
            int amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                player.sendMessage("§cMontant invalide.");
                return true;
            }

            if (MoneyPlugin.getInstance().balanceManager.removeBalance(target, amount)) {
                int blocks = amount / 9;
                int emeralds = amount % 9;

                if (blocks > 0) {
                    target.getInventory().addItem(new ItemStack(Material.EMERALD_BLOCK, blocks));
                }
                if (emeralds > 0) {
                    target.getInventory().addItem(new ItemStack(Material.EMERALD, emeralds));
                }

                player.sendMessage("§a" + amount + "$ convertis en émeraudes pour " + target.getName());
                target.sendMessage("§aVous avez reçu vos émeraudes (" + amount + "$).");
            } else {
                player.sendMessage("§cFonds insuffisants.");
            }
        } catch (NumberFormatException e) {
            player.sendMessage("§cMontant invalide.");
        }

        return true;
    }
}