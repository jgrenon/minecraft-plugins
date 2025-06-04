package avx.money.commands;

import avx.money.MoneyPlugin;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length != 2) {
            player.sendMessage("§cUsage: /pay <joueur> <montant>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage("§cJoueur introuvable.");
            return true;
        }

        try {
            int amount = Integer.parseInt(args[1]);
            if (MoneyPlugin.getInstance().balanceManager.removeBalance(player, amount)) {
                MoneyPlugin.getInstance().balanceManager.addBalance(target, amount);
                player.sendMessage("§aVous avez envoyé " + amount + "$ à " + target.getName());
                target.sendMessage("§a" + player.getName() + " vous a donné " + amount + "$.");
            } else {
                player.sendMessage("§cFonds insuffisants.");
            }
        } catch (NumberFormatException e) {
            player.sendMessage("§cMontant invalide.");
        }

        return true;
    }
}
