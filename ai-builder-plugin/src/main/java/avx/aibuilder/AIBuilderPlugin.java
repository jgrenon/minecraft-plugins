package avx.aibuilder;

import avx.aibuilder.commands.BuildCommand;
import avx.aibuilder.commands.AICommand;
import avx.aibuilder.listeners.PlayerListener;
import avx.aibuilder.managers.AIManager;
import avx.aibuilder.managers.BuildManager;
import avx.aibuilder.managers.ConfigManager;
import avx.aibuilder.managers.TemplateManager;
import avx.aibuilder.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AIBuilderPlugin extends JavaPlugin {
    
    private static AIBuilderPlugin instance;
    private ConfigManager configManager;
    private AIManager aiManager;
    private BuildManager buildManager;
    private TemplateManager templateManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.templateManager = new TemplateManager(this);
        this.buildManager = new BuildManager(this);
        this.aiManager = new AIManager(this);
        
        // Load configuration and templates
        configManager.loadConfig();
        templateManager.loadTemplates();
        
        // Register event listeners
        registerListeners();
        
        // Register commands
        registerCommands();
        
        getLogger().info("AI Builder Plugin enabled!");
        getLogger().info("Ready to interpret prompts and build amazing structures!");
    }
    
    @Override
    public void onDisable() {
        // Cancel any ongoing builds
        if (buildManager != null) {
            buildManager.cancelAllBuilds();
        }
        
        getLogger().info("AI Builder Plugin disabled!");
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }
    
    private void registerCommands() {
        getCommand("build").setExecutor(new BuildCommand(this));
        getCommand("ai").setExecutor(new AICommand(this));
    }
    
    // Getters
    public static AIBuilderPlugin getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public AIManager getAIManager() {
        return aiManager;
    }
    
    public BuildManager getBuildManager() {
        return buildManager;
    }
    
    public TemplateManager getTemplateManager() {
        return templateManager;
    }
} 