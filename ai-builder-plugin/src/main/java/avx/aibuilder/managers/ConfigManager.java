package avx.aibuilder.managers;

import avx.aibuilder.AIBuilderPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    
    private final AIBuilderPlugin plugin;
    private FileConfiguration config;
    
    // Default values
    private int maxBuildSize = 50;
    private int buildCooldown = 30; // seconds
    private boolean enableBuildLimits = true;
    private int maxConcurrentBuilds = 3;
    private boolean enableProgressMessages = true;
    
    // OpenAI settings
    private String openAIApiKey = "";
    private String openAIModel = "gpt-4o-mini";
    private boolean enableOpenAI = true;
    
    public ConfigManager(AIBuilderPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
        
        // Load configuration values
        maxBuildSize = config.getInt("build.max-size", 50);
        buildCooldown = config.getInt("build.cooldown", 30);
        enableBuildLimits = config.getBoolean("build.enable-limits", true);
        maxConcurrentBuilds = config.getInt("build.max-concurrent", 3);
        enableProgressMessages = config.getBoolean("messages.progress", true);
        
        // OpenAI settings
        openAIApiKey = config.getString("openai.api-key", "");
        openAIModel = config.getString("openai.model", "gpt-4o-mini");
        enableOpenAI = config.getBoolean("openai.enabled", true);
        
        plugin.getLogger().info("Configuration loaded successfully!");
    }
    
    public int getMaxBuildSize() {
        return maxBuildSize;
    }
    
    public int getBuildCooldown() {
        return buildCooldown;
    }
    
    public boolean isBuildLimitsEnabled() {
        return enableBuildLimits;
    }
    
    public int getMaxConcurrentBuilds() {
        return maxConcurrentBuilds;
    }
    
    public boolean isProgressMessagesEnabled() {
        return enableProgressMessages;
    }
    
    public String getOpenAIApiKey() {
        return openAIApiKey;
    }
    
    public String getOpenAIModel() {
        return openAIModel;
    }
    
    public boolean isOpenAIEnabled() {
        return enableOpenAI;
    }
} 