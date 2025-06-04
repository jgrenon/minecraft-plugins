package avx.money.commands;

import avx.money.MoneyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public class MoneyCommand implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length == 0) {
            int balance = MoneyPlugin.getInstance().balanceManager.getBalance(player);
            player.sendMessage("§aVotre argent: " + balance + "$");
            return true;
        }

        if (args[0].equalsIgnoreCase("set") && args.length == 3) {
            if (!player.isOp()) {
                player.sendMessage("§cCommande réservée aux opérateurs.");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage("§cJoueur introuvable.");
                return true;
            }
            try {
                int amount = Integer.parseInt(args[2]);
                MoneyPlugin.getInstance().balanceManager.setBalance(target, amount);
                target.sendMessage("§aVous avez " + amount + "$.");
                player.sendMessage("§aArgent de " + target.getName() + " défini à " + amount + "$.");
            } catch (NumberFormatException e) {
                player.sendMessage("§cMontant invalide.");
            }
            return true;
        }

        return false;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
}
