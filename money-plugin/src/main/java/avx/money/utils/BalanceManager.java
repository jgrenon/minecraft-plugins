package avx.money.utils;

import avx.money.MoneyPlugin;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.HashMap;

public class BalanceManager {

    private final MoneyPlugin plugin;
    private final File file;
    private final FileConfiguration config;
    private final HashMap<UUID, Integer> balances = new HashMap<>();

    public BalanceManager(MoneyPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "balances.yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        load();
    }

    public void load() {
        for (String key : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                int balance = config.getInt(key);
                balances.put(uuid, balance);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save() {
        for (UUID uuid : balances.keySet()) {
            config.set(uuid.toString(), balances.get(uuid));
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getBalance(OfflinePlayer player) {
        return balances.getOrDefault(player.getUniqueId(), 0);
    }

    public void setBalance(OfflinePlayer player, int amount) {
        balances.put(player.getUniqueId(), amount);
    }

    public void addBalance(OfflinePlayer player, int amount) {
        setBalance(player, getBalance(player) + amount);
    }

    public boolean removeBalance(OfflinePlayer player, int amount) {
        int current = getBalance(player);
        if (current >= amount) {
            setBalance(player, current - amount);
            return true;
        }
        return false;
    }
}
