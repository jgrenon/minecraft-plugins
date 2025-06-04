package avx.physics;

import avx.physics.commands.*;
import avx.physics.listeners.*;
import avx.physics.managers.*;
import avx.physics.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class PhysicsPlugin extends JavaPlugin {
    
    private static PhysicsPlugin instance;
    private ConfigManager configManager;
    private PhysicsManager physicsManager;
    private StructureManager structureManager;
    private GravityManager gravityManager;
    private PerformanceManager performanceManager;
    private BukkitTask physicsTask;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.performanceManager = new PerformanceManager(this);
        this.structureManager = new StructureManager(this);
        this.gravityManager = new GravityManager(this);
        this.physicsManager = new PhysicsManager(this);
        
        // Load configuration
        configManager.loadConfig();
        
        // Register event listeners
        registerListeners();
        
        // Register commands
        registerCommands();
        
        // Start physics calculations
        startPhysicsLoop();
        
        getLogger().info("Realistic Physics Plugin enabled!");
        getLogger().info("Physics system: " + (configManager.isEnabled() ? "ENABLED" : "DISABLED"));
    }
    
    @Override
    public void onDisable() {
        // Stop physics loop
        if (physicsTask != null && !physicsTask.isCancelled()) {
            physicsTask.cancel();
        }
        
        // Clear any pending physics calculations
        if (physicsManager != null) {
            physicsManager.clearAll();
        }
        
        getLogger().info("Realistic Physics Plugin disabled!");
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new BlockPhysicsListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractionListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
    }
    
    private void registerCommands() {
        getCommand("physics").setExecutor(new PhysicsCommand(this));
        getCommand("gravity").setExecutor(new GravityCommand(this));
        getCommand("structure").setExecutor(new StructureCommand(this));
    }
    
    private void startPhysicsLoop() {
        int interval = configManager.getCheckInterval();
        physicsTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (configManager.isEnabled() && performanceManager.canProcessPhysics()) {
                physicsManager.processPhysics();
            }
        }, 20L, interval);
    }
    
    public void reloadPlugin() {
        // Stop current physics loop
        if (physicsTask != null && !physicsTask.isCancelled()) {
            physicsTask.cancel();
        }
        
        // Reload configuration
        configManager.loadConfig();
        
        // Restart physics loop
        startPhysicsLoop();
        
        getLogger().info("Plugin reloaded!");
    }
    
    // Getters
    public static PhysicsPlugin getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public PhysicsManager getPhysicsManager() {
        return physicsManager;
    }
    
    public StructureManager getStructureManager() {
        return structureManager;
    }
    
    public GravityManager getGravityManager() {
        return gravityManager;
    }
    
    public PerformanceManager getPerformanceManager() {
        return performanceManager;
    }
} 