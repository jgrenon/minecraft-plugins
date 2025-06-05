package avx.aibuilder.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageUtils {
    
    private static final String PREFIX = ChatColor.BLUE + "[AI Builder] " + ChatColor.RESET;
    
    public static void sendSuccess(CommandSender sender, String message) {
        sender.sendMessage(PREFIX + ChatColor.GREEN + message);
    }
    
    public static void sendError(CommandSender sender, String message) {
        sender.sendMessage(PREFIX + ChatColor.RED + message);
    }
    
    public static void sendInfo(CommandSender sender, String message) {
        sender.sendMessage(PREFIX + ChatColor.YELLOW + message);
    }
    
    public static void sendWarning(CommandSender sender, String message) {
        sender.sendMessage(PREFIX + ChatColor.GOLD + message);
    }
    
    public static void sendPlain(CommandSender sender, String message) {
        sender.sendMessage(PREFIX + ChatColor.WHITE + message);
    }
} 