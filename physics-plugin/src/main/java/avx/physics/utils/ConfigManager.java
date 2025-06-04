package avx.physics.utils;

import avx.physics.PhysicsPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigManager {
    
    private final PhysicsPlugin plugin;
    private FileConfiguration config;
    
    public ConfigManager(PhysicsPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }
    
    // Main settings
    public boolean isEnabled() {
        return config.getBoolean("enabled", true);
    }
    
    // Physics settings
    public int getMaxSupportDistance() {
        return config.getInt("physics.max-support-distance", 10);
    }
    
    public int getMinSupportBlocks() {
        return config.getInt("physics.min-support-blocks", 2);
    }
    
    public int getCheckInterval() {
        return config.getInt("physics.check-interval", 10);
    }
    
    public int getMaxBlocksPerTick() {
        return config.getInt("physics.max-blocks-per-tick", 50);
    }
    
    public boolean isChainReactionsEnabled() {
        return config.getBoolean("physics.chain-reactions", true);
    }
    
    public int getMaxChainDistance() {
        return config.getInt("physics.max-chain-distance", 15);
    }
    
    // Gravity settings
    public double getDefaultGravityStrength() {
        return config.getDouble("gravity.default-strength", 1.0);
    }
    
    public boolean isMomentumEnabled() {
        return config.getBoolean("gravity.momentum", true);
    }
    
    public double getBounceFactor() {
        return config.getDouble("gravity.bounce-factor", 0.3);
    }
    
    public double getAirResistance() {
        return config.getDouble("gravity.air-resistance", 0.02);
    }
    
    // Block settings
    public Set<Material> getPhysicsBlocks() {
        List<String> materialNames = config.getStringList("physics-blocks");
        return materialNames.stream()
                .map(name -> {
                    try {
                        return Material.valueOf(name.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid material in physics-blocks: " + name);
                        return null;
                    }
                })
                .filter(material -> material != null)
                .collect(Collectors.toSet());
    }
    
    public Set<Material> getSupportBlocks() {
        List<String> materialNames = config.getStringList("support-blocks");
        return materialNames.stream()
                .map(name -> {
                    try {
                        return Material.valueOf(name.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid material in support-blocks: " + name);
                        return null;
                    }
                })
                .filter(material -> material != null)
                .collect(Collectors.toSet());
    }
    
    // Performance settings
    public int getMaxChunks() {
        return config.getInt("performance.max-chunks", 5);
    }
    
    public double getMinTPS() {
        return config.getDouble("performance.min-tps", 15.0);
    }
    
    public boolean isAsyncProcessingEnabled() {
        return config.getBoolean("performance.async-processing", false);
    }
    
    // Effects settings
    public boolean isFallingParticlesEnabled() {
        return config.getBoolean("effects.falling-particles", true);
    }
    
    public boolean isImpactParticlesEnabled() {
        return config.getBoolean("effects.impact-particles", true);
    }
    
    public boolean isSoundsEnabled() {
        return config.getBoolean("effects.sounds", true);
    }
    
    public float getSoundVolume() {
        return (float) config.getDouble("effects.sound-volume", 0.5);
    }
    
    // World settings
    public List<String> getEnabledWorlds() {
        return config.getStringList("worlds.enabled-worlds");
    }
    
    public List<String> getDisabledWorlds() {
        return config.getStringList("worlds.disabled-worlds");
    }
    
    public boolean isWorldEnabled(String worldName) {
        List<String> enabledWorlds = getEnabledWorlds();
        List<String> disabledWorlds = getDisabledWorlds();
        
        // If disabled worlds contains this world, it's disabled
        if (disabledWorlds.contains(worldName)) {
            return false;
        }
        
        // If enabled worlds is empty, all worlds are enabled (except disabled ones)
        // If enabled worlds is not empty, only those worlds are enabled
        return enabledWorlds.isEmpty() || enabledWorlds.contains(worldName);
    }
    
    // Debug settings
    public boolean isDebugEnabled() {
        return config.getBoolean("debug.enabled", false);
    }
    
    public boolean isShowSupportLinesEnabled() {
        return config.getBoolean("debug.show-support-lines", false);
    }
    
    public boolean isLogCalculationsEnabled() {
        return config.getBoolean("debug.log-calculations", false);
    }
} 