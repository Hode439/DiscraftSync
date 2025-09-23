package com.hode.discraftsync.managers;

import com.hode.discraftsync.DiscraftSync;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class LinkManager {
    
    private final DiscraftSync plugin;
    private final ConfigManager configManager;
    
    // Cache for linked accounts (Discord ID -> IGN)
    private final Map<String, String> linkedAccounts = new HashMap<>();
    
    // Reverse cache for quick IGN lookups (IGN -> Discord ID)
    private final Map<String, String> reverseLinkedAccounts = new HashMap<>();
    
    public LinkManager(DiscraftSync plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        loadLinkedAccounts();
    }
    
    private void loadLinkedAccounts() {
        FileConfiguration config = configManager.getLinkedAccountsConfig();
        
        if (config.contains("linkedAccounts")) {
            for (String discordId : config.getConfigurationSection("linkedAccounts").getKeys(false)) {
                String ign = config.getString("linkedAccounts." + discordId + ".ign");
                if (ign != null) {
                    linkedAccounts.put(discordId, ign);
                    reverseLinkedAccounts.put(ign.toLowerCase(), discordId);
                }
            }
        }
        
        plugin.getLogger().info("Loaded " + linkedAccounts.size() + " linked accounts");
    }
    
    public void saveData() {
        FileConfiguration config = configManager.getLinkedAccountsConfig();
        
        // Clear existing data
        config.set("linkedAccounts", null);
        
        // Save current data
        for (Map.Entry<String, String> entry : linkedAccounts.entrySet()) {
            String discordId = entry.getKey();
            String ign = entry.getValue();
            
            config.set("linkedAccounts." + discordId + ".ign", ign);
            config.set("linkedAccounts." + discordId + ".linked-date", 
                config.getString("linkedAccounts." + discordId + ".linked-date", Instant.now().toString()));
            config.set("linkedAccounts." + discordId + ".last-sync", Instant.now().toString());
        }
        
        // Update metadata
        config.set("metadata.last-updated", Instant.now().toString());
        config.set("metadata.total-links", linkedAccounts.size());
        
        configManager.saveLinkedAccountsConfig();
    }
    
    public boolean linkAccount(String discordId, String ign) {
        // Check if Discord ID is already linked
        if (linkedAccounts.containsKey(discordId)) {
            return false;
        }
        
        // Check if IGN is already linked
        if (reverseLinkedAccounts.containsKey(ign.toLowerCase())) {
            return false;
        }
        
        // Add to caches
        linkedAccounts.put(discordId, ign);
        reverseLinkedAccounts.put(ign.toLowerCase(), discordId);
        
        // Save to config
        FileConfiguration config = configManager.getLinkedAccountsConfig();
        config.set("linkedAccounts." + discordId + ".ign", ign);
        config.set("linkedAccounts." + discordId + ".linked-date", Instant.now().toString());
        config.set("linkedAccounts." + discordId + ".last-sync", Instant.now().toString());
        
        saveData();
        
        plugin.getLogger().info("Linked account: Discord " + discordId + " -> IGN " + ign);
        return true;
    }
    
    public boolean unlinkAccount(String discordId) {
        String ign = linkedAccounts.get(discordId);
        if (ign == null) {
            return false;
        }
        
        // Remove from caches
        linkedAccounts.remove(discordId);
        reverseLinkedAccounts.remove(ign.toLowerCase());
        
        // Remove from config
        FileConfiguration config = configManager.getLinkedAccountsConfig();
        config.set("linkedAccounts." + discordId, null);
        
        saveData();
        
        plugin.getLogger().info("Unlinked account: Discord " + discordId + " (was " + ign + ")");
        return true;
    }
    
    public boolean unlinkAccountByIgn(String ign) {
        String discordId = reverseLinkedAccounts.get(ign.toLowerCase());
        if (discordId == null) {
            return false;
        }
        
        return unlinkAccount(discordId);
    }
    
    public String getLinkedIgn(String discordId) {
        return linkedAccounts.get(discordId);
    }
    
    public String getLinkedDiscordId(String ign) {
        return reverseLinkedAccounts.get(ign.toLowerCase());
    }
    
    public boolean isDiscordLinked(String discordId) {
        return linkedAccounts.containsKey(discordId);
    }
    
    public boolean isIgnLinked(String ign) {
        return reverseLinkedAccounts.containsKey(ign.toLowerCase());
    }
    
    public Map<String, String> getLinkedAccounts() {
        return new HashMap<>(linkedAccounts);
    }
    
    public int getTotalLinkedAccounts() {
        return linkedAccounts.size();
    }
    
    public void updateLastSync(String discordId) {
        if (linkedAccounts.containsKey(discordId)) {
            FileConfiguration config = configManager.getLinkedAccountsConfig();
            config.set("linkedAccounts." + discordId + ".last-sync", Instant.now().toString());
            configManager.saveLinkedAccountsConfig();
        }
    }
}