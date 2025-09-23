package com.hode.discraftsync;

import com.hode.discraftsync.commands.SyncCommand;
import com.hode.discraftsync.discord.DiscordBot;
import com.hode.discraftsync.listeners.PlayerJoinListener;
import com.hode.discraftsync.managers.ConfigManager;
import com.hode.discraftsync.managers.LinkManager;
import com.hode.discraftsync.managers.RoleManager;
import com.hode.discraftsync.managers.VerificationManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DiscraftSync extends JavaPlugin {
    
    private static DiscraftSync instance;
    
    private ConfigManager configManager;
    private LinkManager linkManager;
    private RoleManager roleManager;
    private VerificationManager verificationManager;
    private DiscordBot discordBot;
    
    @Override
    public void onEnable() {
        instance = this;
        
        getLogger().info("Starting DiscraftSync v" + getDescription().getVersion());
        getLogger().info("Plugin made with 💖 by Hode");
        
        // Check for LuckPerms
        if (getServer().getPluginManager().getPlugin("LuckPerms") == null) {
            getLogger().severe("LuckPerms not found! This plugin requires LuckPerms to work.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Initialize managers
        initializeManagers();
        
        // Initialize Discord bot
        initializeDiscordBot();
        
        // Register commands and listeners
        registerCommandsAndListeners();
        
        getLogger().info("DiscraftSync has been enabled successfully!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("Shutting down DiscraftSync...");
        
        // Shutdown Discord bot
        if (discordBot != null) {
            discordBot.shutdown();
        }
        
        // Save data
        if (linkManager != null) {
            linkManager.saveData();
        }
        
        getLogger().info("DiscraftSync has been disabled.");
    }
    
    private void initializeManagers() {
        configManager = new ConfigManager(this);
        linkManager = new LinkManager(this);
        roleManager = new RoleManager(this);
        verificationManager = new VerificationManager(this);
    }
    
    private void initializeDiscordBot() {
        String token = configManager.getDiscordToken();
        if (token == null || token.equals("YOUR_DISCORD_BOT_TOKEN_HERE")) {
            getLogger().severe("Discord bot token not configured! Please set your bot token in config.yml");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        discordBot = new DiscordBot(this);
        if (!discordBot.initialize()) {
            getLogger().severe("Failed to initialize Discord bot! Check your token and try again.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }
    
    private void registerCommandsAndListeners() {
        // Register commands
        getCommand("discraftsync").setExecutor(new SyncCommand(this));
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }
    
    // Getters for managers
    public static DiscraftSync getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public LinkManager getLinkManager() {
        return linkManager;
    }
    
    public RoleManager getRoleManager() {
        return roleManager;
    }
    
    public VerificationManager getVerificationManager() {
        return verificationManager;
    }
    
    public DiscordBot getDiscordBot() {
        return discordBot;
    }
}