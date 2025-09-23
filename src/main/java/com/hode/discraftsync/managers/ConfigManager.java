package com.hode.discraftsync.managers;

import com.hode.discraftsync.DiscraftSync;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    
    private final DiscraftSync plugin;
    private FileConfiguration config;
    private FileConfiguration rolesConfig;
    private FileConfiguration linkedAccountsConfig;
    
    private File rolesFile;
    private File linkedAccountsFile;
    
    public ConfigManager(DiscraftSync plugin) {
        this.plugin = plugin;
        loadConfigurations();
    }
    
    private void loadConfigurations() {
        // Save default config if it doesn't exist
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
        
        // Load roles.yml
        rolesFile = new File(plugin.getDataFolder(), "roles.yml");
        if (!rolesFile.exists()) {
            plugin.saveResource("roles.yml", false);
        }
        rolesConfig = YamlConfiguration.loadConfiguration(rolesFile);
        
        // Load linkedAccounts.yml
        linkedAccountsFile = new File(plugin.getDataFolder(), "linkedAccounts.yml");
        if (!linkedAccountsFile.exists()) {
            plugin.saveResource("linkedAccounts.yml", false);
        }
        linkedAccountsConfig = YamlConfiguration.loadConfiguration(linkedAccountsFile);
    }
    
    public void reloadConfigurations() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        rolesConfig = YamlConfiguration.loadConfiguration(rolesFile);
        linkedAccountsConfig = YamlConfiguration.loadConfiguration(linkedAccountsFile);
    }
    
    public void saveRolesConfig() {
        try {
            rolesConfig.save(rolesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save roles.yml: " + e.getMessage());
        }
    }
    
    public void saveLinkedAccountsConfig() {
        try {
            linkedAccountsConfig.save(linkedAccountsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save linkedAccounts.yml: " + e.getMessage());
        }
    }
    
    // Discord settings
    public String getDiscordToken() {
        return config.getString("discord.token", "");
    }
    
    public String getGuildId() {
        return config.getString("discord.guild-id", "");
    }
    
    // Server settings
    public String getServerIp() {
        return config.getString("server.ip", "hode.lol");
    }
    
    public String getServerName() {
        return config.getString("server.name", "Hode's Server");
    }
    
    // Sync settings
    public String getAdminRoleId() {
        return config.getString("sync.admin-role", "");
    }
    
    public String getDefaultDiscordRole() {
        return config.getString("sync.default.discord-role", "");
    }
    
    public String getDefaultLuckPermsGroup() {
        return config.getString("sync.default.luckperms-group", "default");
    }
    
    // Logging settings
    public String getLoggingChannelId() {
        return config.getString("logging.channel-id", "");
    }
    
    public boolean isLoggingEnabled() {
        return config.getBoolean("logging.enabled", true);
    }
    
    // Verification settings
    public int getCodeLength() {
        return config.getInt("verification.code-length", 4);
    }
    
    public int getCodeExpirySeconds() {
        return config.getInt("verification.expires-seconds", 300);
    }
    
    public boolean useUppercase() {
        return config.getBoolean("verification.use-uppercase", true);
    }
    
    public boolean useNumbers() {
        return config.getBoolean("verification.use-numbers", true);
    }
    
    // Message settings
    public String getPrefix() {
        return config.getString("messages.prefix", "&8[&9Discraft&bSync&8]&r ");
    }
    
    public boolean isColoredChatEnabled() {
        return config.getBoolean("messages.colored-chat", true);
    }
    
    // Advanced settings
    public boolean isDebugEnabled() {
        return config.getBoolean("advanced.debug", false);
    }
    
    public boolean isSyncOnJoinEnabled() {
        return config.getBoolean("advanced.sync-on-join", true);
    }
    
    public int getAutoSyncInterval() {
        return config.getInt("advanced.auto-sync-interval", 30);
    }
    
    public int getMaxVerificationAttempts() {
        return config.getInt("advanced.max-verification-attempts", 3);
    }
    
    // Role and linked accounts configurations
    public FileConfiguration getRolesConfig() {
        return rolesConfig;
    }
    
    public FileConfiguration getLinkedAccountsConfig() {
        return linkedAccountsConfig;
    }
}