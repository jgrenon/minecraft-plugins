package avx.duel;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.*;
import java.io.*;

public class DuelPlugin extends JavaPlugin implements Listener {

    private final Map<Player, Player> pendingChallenges = new HashMap<>();
    private final Map<Player, Long> challengeTimestamps = new HashMap<>();
    public final Map<Player, Player> ongoingDuels = new HashMap<>();
    public final Map<UUID, Stats> playerStats = new HashMap<>();
    private final Map<String, DuelArena> arenas = new HashMap<>();
    private final Map<UUID, Location> originalLocations = new HashMap<>();

    private int duelTimeoutSeconds;
    private int countdownSeconds;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadArenas();
        loadPlayerStats();

        getServer().getPluginManager().registerEvents(new DuelListener(), this);
        getServer().getPluginManager().registerEvents(new LeaderboardGUIListener(), this);
        getServer().getPluginManager().registerEvents(new DuelCommandFilter(), this);
        getServer().getPluginManager().registerEvents(this, this);

        duelTimeoutSeconds = getConfig().getInt("duel_timeout_seconds", 30);
        countdownSeconds = getConfig().getInt("countdown_seconds", 5);
        this.getCommand("duel").setExecutor(this);

        this.startDuelTimeoutTask();
        this.startAutoSaveTask();
    }

    public void loadArenas() {
        arenas.clear();
        ConfigurationSection section = getConfig().getConfigurationSection("arenas");
        if (section == null)
            return;

        for (String name : section.getKeys(false)) {
            Location slot1 = loadLocation(section.getConfigurationSection(name + ".slot1"));
            Location slot2 = loadLocation(section.getConfigurationSection(name + ".slot2"));
            if (slot1 != null && slot2 != null) {
                arenas.put(name, new DuelArena(slot1, slot2));
            }
        }
    }

    private Location loadLocation(ConfigurationSection cs) {
        if (cs == null)
            return null;
        World world = Bukkit.getWorld(cs.getString("world"));
        double x = cs.getDouble("x");
        double y = cs.getDouble("y");
        double z = cs.getDouble("z");
        return (world == null) ? null : new Location(world, x, y, z);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            String arg = args[0];

            if (arg.equalsIgnoreCase("accept")) {
                if (!pendingChallenges.containsKey(player)) {
                    player.sendMessage("You have no pending duel requests.");
                    return true;
                }

                Player challenger = pendingChallenges.remove(player);
                challengeTimestamps.remove(player);

                if (challenger == null || !challenger.isOnline()) {
                    player.sendMessage("The challenger is no longer online.");
                    return true;
                }

                player.sendMessage("Duel starting in " + countdownSeconds + " seconds...");
                challenger.sendMessage(
                        player.getName() + " has accepted your duel! Starting in " + countdownSeconds + " seconds...");

                DuelArena arena = arenas.getOrDefault("default", null);
                if (arena == null) {
                    player.sendMessage("No arena is configured.");
                    return true;
                }

                new BukkitRunnable() {
                    int seconds = countdownSeconds;

                    @Override
                    public void run() {
                        if (seconds <= 0) {
                            originalLocations.put(challenger.getUniqueId(), challenger.getLocation());
                            originalLocations.put(player.getUniqueId(), player.getLocation());
                            challenger.teleport(arena.slot1);
                            player.teleport(arena.slot2);
                            challenger.sendMessage(Component.text("Fight!").color(NamedTextColor.RED));
                            player.sendMessage(Component.text("Fight!").color(NamedTextColor.RED));
                            
                            // Add both players to ongoing duels map
                            ongoingDuels.put(challenger, player);
                            ongoingDuels.put(player, challenger);
                            
                            // Set up scoreboards for both players
                            setupScoreboard(challenger, player);
                            setupScoreboard(player, challenger);
                            
                            cancel();
                            return;
                        }
                        challenger.sendMessage(Component.text("Teleporting in " + seconds + "...").color(NamedTextColor.YELLOW));
                        player.sendMessage(Component.text("Teleporting in " + seconds + "...").color(NamedTextColor.YELLOW));
                        seconds--;
                    }
                }.runTaskTimer(this, 0L, 20L); // 20 ticks = 1 second

                return true;
            }

            if (arg.equalsIgnoreCase("deny")) {
                if (!pendingChallenges.containsKey(player)) {
                    player.sendMessage("You have no duel to deny.");
                    return true;
                }

                Player challenger = pendingChallenges.remove(player);
                challengeTimestamps.remove(player);

                player.sendMessage("You have denied the duel request.");
                if (challenger != null && challenger.isOnline()) {
                    challenger.sendMessage(player.getName() + " has denied your duel request.");
                }

                return true;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("leaderboard")) {
                openLeaderboardGUI(player);
                return true;
            }

            if (args.length == 3 && args[0].equalsIgnoreCase("setarena")) {
                String name = args[1];
                String slot = args[2].toLowerCase();

                if (!slot.equals("slot1") && !slot.equals("slot2")) {
                    player.sendMessage("Slot must be 'slot1' or 'slot2'.");
                    return true;
                }

                Location loc = player.getLocation();
                String base = "arenas." + name + "." + slot;
                getConfig().set(base + ".world", loc.getWorld().getName());
                getConfig().set(base + ".x", loc.getX());
                getConfig().set(base + ".y", loc.getY());
                getConfig().set(base + ".z", loc.getZ());

                saveConfig();
                loadArenas();

                player.sendMessage("Arena " + name + " " + slot + " set to your current location.");
                return true;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("forfeit")) {
                if (!ongoingDuels.containsKey(player) && !ongoingDuels.containsValue(player)) {
                    player.sendMessage(Component.text("You are not in a duel.").color(NamedTextColor.RED));
                    return true;
                }

                Player opponent = ongoingDuels.get(player);
                if (opponent == null) {
                    // Try reverse map lookup
                    for (Map.Entry<Player, Player> entry : ongoingDuels.entrySet()) {
                        if (entry.getValue().equals(player)) {
                            opponent = entry.getKey();
                            break;
                        }
                    }
                }

                if (opponent == null || !opponent.isOnline()) {
                    player.sendMessage(Component.text("Could not resolve opponent.").color(NamedTextColor.RED));
                    ongoingDuels.remove(player);
                    return true;
                }

                // Use the new clean ending method for forfeit
                endDuelCleanly(player, opponent, "forfeit");

                // Send forfeit-specific messages
                player.sendMessage(Component.text("You forfeited the duel.").color(NamedTextColor.RED));
                opponent.sendMessage(Component.text(player.getName() + " forfeited. You win!").color(NamedTextColor.GREEN));

                Bukkit.broadcast(Component.text("[DUEL] ").color(NamedTextColor.GOLD)
                        .append(Component.text(opponent.getName()).color(NamedTextColor.YELLOW))
                        .append(Component.text(" won by forfeit against ").color(NamedTextColor.WHITE))
                        .append(Component.text(player.getName()).color(NamedTextColor.YELLOW))
                        .append(Component.text("!").color(NamedTextColor.WHITE)));

                return true;
            }

            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                player.sendMessage(Component.text("---------- /duel Help ----------").color(NamedTextColor.GOLD));
                player.sendMessage(Component.text("/duel <player> ").color(NamedTextColor.YELLOW)
                        .append(Component.text("- Challenge another player to a duel.").color(NamedTextColor.WHITE)));
                player.sendMessage(
                        Component.text("/duel accept ").color(NamedTextColor.YELLOW)
                        .append(Component.text("- Accept a pending duel challenge.").color(NamedTextColor.WHITE)));
                player.sendMessage(
                        Component.text("/duel deny ").color(NamedTextColor.YELLOW)
                        .append(Component.text("- Deny a pending duel challenge.").color(NamedTextColor.WHITE)));
                player.sendMessage(Component.text("/duel forfeit ").color(NamedTextColor.YELLOW)
                        .append(Component.text("- Forfeit your current duel (only usable during a duel).").color(NamedTextColor.WHITE)));
                player.sendMessage(Component.text("/duel leaderboard ").color(NamedTextColor.YELLOW)
                        .append(Component.text("- View the top duelists in a GUI.").color(NamedTextColor.WHITE)));
                player.sendMessage(Component.text("/duel setarena <name> <slot1|slot2> ").color(NamedTextColor.YELLOW)
                        .append(Component.text("- Set arena teleport points at your current location.").color(NamedTextColor.WHITE)));
                player.sendMessage(Component.text("-------------------------------").color(NamedTextColor.GOLD));
                return true;
            }

            // Duel <player>
            Player target = Bukkit.getPlayer(arg);
            if (target == null || !target.isOnline()) {
                player.sendMessage("That player is not online.");
                return true;
            }

            if (target.equals(player)) {
                player.sendMessage("You cannot duel yourself.");
                return true;
            }

            pendingChallenges.put(target, player);
            challengeTimestamps.put(target, System.currentTimeMillis());

            player.sendMessage("You have challenged " + target.getName() + " to a duel.");
            target.sendMessage(player.getName() + " has challenged you to a duel! Type /duel accept or /duel deny.");
            return true;
        }

        player.sendMessage("Usage: /duel <player> | accept | deny");
        return true;
    }

    @Override
    public void onDisable() {
        savePlayerStats();
        pendingChallenges.clear();
        challengeTimestamps.clear();
        // Clean up any ongoing duels
        for (Player player : ongoingDuels.keySet()) {
            endDuelCleanly(player, null, "Server shutdown");
        }
    }

    public void updateStats(Player player, boolean won) {
        UUID id = player.getUniqueId();
        Stats stats = playerStats.getOrDefault(id, new Stats());
        if (won)
            stats.wins++;
        else
            stats.losses++;
        playerStats.put(id, stats);
    }

    public void setupScoreboard(Player p1, Player p2) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective obj = board.registerNewObjective("duel", Criteria.DUMMY, Component.text("Duel vs " + p2.getName()).color(NamedTextColor.GOLD));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.getScore("§aYou: " + p1.getName()).setScore(1);
        obj.getScore("§cEnemy: " + p2.getName()).setScore(0);
        p1.setScoreboard(board);
    }

    public void openLeaderboardGUI(Player viewer) {
        int size = Math.min(54, Math.max(9, ((playerStats.size() + 8) / 9) * 9)); // Dynamic size, max 6 rows
        Inventory gui = Bukkit.createInventory(null, size, Component.text("Duel Leaderboard").color(NamedTextColor.GOLD));

        List<Map.Entry<UUID, Stats>> topStats = playerStats.entrySet().stream()
                .filter(entry -> entry.getValue().wins > 0 || entry.getValue().losses > 0) // Only show players with games
                .sorted((a, b) -> {
                    // Sort by wins first, then by win ratio
                    int winsCompare = Integer.compare(b.getValue().wins, a.getValue().wins);
                    if (winsCompare != 0) return winsCompare;
                    
                    double ratioA = a.getValue().losses == 0 ? a.getValue().wins : (double) a.getValue().wins / (a.getValue().wins + a.getValue().losses);
                    double ratioB = b.getValue().losses == 0 ? b.getValue().wins : (double) b.getValue().wins / (b.getValue().wins + b.getValue().losses);
                    return Double.compare(ratioB, ratioA);
                })
                .limit(size)
                .toList();

        int i = 0;
        for (Map.Entry<UUID, Stats> entry : topStats) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(entry.getKey());
            Stats s = entry.getValue();
            
            // Skip if player name is null (deleted player)
            if (p.getName() == null) continue;

            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(p);
                String playerName = p.getName() != null ? p.getName() : "Unknown Player";
                meta.displayName(Component.text("#" + (i + 1) + " " + playerName).color(NamedTextColor.YELLOW));
                
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("Wins: ").color(NamedTextColor.GREEN).append(Component.text(s.wins)));
                lore.add(Component.text("Losses: ").color(NamedTextColor.RED).append(Component.text(s.losses)));
                
                double winRate = (s.wins + s.losses) > 0 ? (double) s.wins / (s.wins + s.losses) * 100 : 0;
                lore.add(Component.text("Win Rate: ").color(NamedTextColor.AQUA)
                        .append(Component.text(String.format("%.1f%%", winRate))));
                        
                if (p.isOnline()) {
                    lore.add(Component.text("Status: ").color(NamedTextColor.GRAY)
                            .append(Component.text("Online").color(NamedTextColor.GREEN)));
                } else {
                    lore.add(Component.text("Status: ").color(NamedTextColor.GRAY)
                            .append(Component.text("Offline").color(NamedTextColor.RED)));
                }
                
                meta.lore(lore);
                skull.setItemMeta(meta);
            }

            gui.setItem(i++, skull);
            if (i >= size) break; // Safety check
        }

        viewer.openInventory(gui);
    }

    public void returnToOriginal(Player player) {
        Location loc = originalLocations.remove(player.getUniqueId());
        if (loc != null) {
            player.teleport(loc);
        }
    }

    private void startDuelTimeoutTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                List<Player> toRemove = new ArrayList<>();

                for (Map.Entry<Player, Long> entry : challengeTimestamps.entrySet()) {
                    if ((now - entry.getValue()) >= duelTimeoutSeconds * 1000L) {
                        Player challenged = entry.getKey();
                        Player challenger = pendingChallenges.get(challenged);

                        if (challenged != null && challenged.isOnline()) {
                            challenged.sendMessage(Component.text("Your duel request has expired.").color(NamedTextColor.RED));
                        }

                        if (challenger != null && challenger.isOnline()) {
                            challenger.sendMessage(Component.text("Duel request to " + challenged.getName() + " has expired.").color(NamedTextColor.RED));
                        }

                        toRemove.add(challenged);
                    }
                }

                for (Player p : toRemove) {
                    challengeTimestamps.remove(p);
                    pendingChallenges.remove(p);
                }

            }
        }.runTaskTimer(this, 0L, 20L * 60); // Runs every 60 seconds

    }

    private void startAutoSaveTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!playerStats.isEmpty()) {
                    savePlayerStats();
                }
            }
        }.runTaskTimer(this, 20L * 60 * 5, 20L * 60 * 5); // Auto-save every 5 minutes
    }

    private void loadPlayerStats() {
        File statsFile = new File(getDataFolder(), "playerstats.dat");
        if (!statsFile.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(statsFile))) {
            @SuppressWarnings("unchecked")
            Map<UUID, Stats> loadedStats = (Map<UUID, Stats>) ois.readObject();
            playerStats.putAll(loadedStats);
            getLogger().info("Loaded " + playerStats.size() + " player statistics.");
        } catch (Exception e) {
            getLogger().warning("Failed to load player statistics: " + e.getMessage());
        }
    }

    private void savePlayerStats() {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File statsFile = new File(dataFolder, "playerstats.dat");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(statsFile))) {
            oos.writeObject(new HashMap<>(playerStats));
            getLogger().info("Saved " + playerStats.size() + " player statistics.");
        } catch (Exception e) {
            getLogger().severe("Failed to save player statistics: " + e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Handle pending challenges
        pendingChallenges.remove(player);
        challengeTimestamps.remove(player);
        
        // Handle ongoing duels - player loses by disconnect
        if (ongoingDuels.containsKey(player)) {
            Player opponent = ongoingDuels.get(player);
            endDuelCleanly(player, opponent, "disconnect");
        } else if (ongoingDuels.containsValue(player)) {
            // Find the opponent who has this player as their target
            Player opponent = null;
            for (Map.Entry<Player, Player> entry : ongoingDuels.entrySet()) {
                if (entry.getValue().equals(player)) {
                    opponent = entry.getKey();
                    break;
                }
            }
            if (opponent != null) {
                endDuelCleanly(opponent, player, "opponent disconnect");
            }
        }
    }

    public void endDuelCleanly(Player player1, Player player2, String reason) {
        // Determine winner and loser based on reason
        Player winner = null;
        Player loser = null;
        
        if ("disconnect".equals(reason)) {
            loser = player1;
            winner = player2;
        } else if ("opponent disconnect".equals(reason)) {
            winner = player1;
            loser = player2;
        } else if ("death".equals(reason)) {
            loser = player1;
            winner = player2;
        } else if ("forfeit".equals(reason)) {
            loser = player1;
            winner = player2;
        }
        
        // Update stats if we have both players
        if (winner != null && loser != null) {
            updateStats(winner, true);
            updateStats(loser, false);
            
            // Send messages
            if (winner.isOnline()) {
                if ("disconnect".equals(reason) || "opponent disconnect".equals(reason)) {
                    winner.sendMessage(Component.text("You won the duel! Your opponent disconnected.").color(NamedTextColor.GREEN));
                } else {
                    winner.sendMessage(Component.text("You won the duel against " + loser.getName() + "!").color(NamedTextColor.GREEN));
                }
            }
            
            if (loser.isOnline()) {
                if ("disconnect".equals(reason)) {
                    loser.sendMessage(Component.text("You lost the duel by disconnecting.").color(NamedTextColor.RED));
                } else {
                    loser.sendMessage(Component.text("You lost the duel against " + winner.getName() + ".").color(NamedTextColor.RED));
                }
            }
            
            // Broadcast result
            String winnerName = winner.getName();
            String loserName = loser.getName();
            if ("disconnect".equals(reason) || "opponent disconnect".equals(reason)) {
                Bukkit.broadcast(Component.text("[DUEL] ").color(NamedTextColor.GOLD)
                        .append(Component.text(winnerName).color(NamedTextColor.YELLOW))
                        .append(Component.text(" won against ").color(NamedTextColor.WHITE))
                        .append(Component.text(loserName).color(NamedTextColor.YELLOW))
                        .append(Component.text(" (disconnect)!").color(NamedTextColor.WHITE)));
            }
        }
        
        // Clean up duel state completely
        ongoingDuels.remove(player1);
        ongoingDuels.remove(player2);
        
        // Reset scoreboards
        if (player1 != null && player1.isOnline()) {
            player1.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            returnToOriginal(player1);
        }
        if (player2 != null && player2.isOnline()) {
            player2.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            returnToOriginal(player2);
        }
        
        // Save stats immediately
        savePlayerStats();
    }
}