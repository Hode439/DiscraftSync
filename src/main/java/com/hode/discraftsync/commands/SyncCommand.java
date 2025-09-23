package com.hode.discraftsync.commands;

import com.hode.discraftsync.DiscraftSync;
import com.hode.discraftsync.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SyncCommand implements CommandExecutor {
    
    private final DiscraftSync plugin;
    
    public SyncCommand(DiscraftSync plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check permission
        if (!sender.hasPermission("discraftsync.admin")) {
            MessageUtils.sendMessage(sender, "&cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "reload":
                handleReload(sender);
                break;
                
            case "generate":
            case "gen":
                handleGenerate(sender);
                break;
                
            case "info":
            case "status":
                handleInfo(sender);
                break;
                
            case "help":
                sendHelpMessage(sender);
                break;
                
            default:
                // Default to generate if no valid subcommand
                handleGenerate(sender);
                break;
        }
        
        return true;
    }
    
    private void handleReload(CommandSender sender) {
        MessageUtils.sendMessage(sender, "&eReloading DiscraftSync configuration...");
        
        try {
            plugin.getConfigManager().reloadConfigurations();
            plugin.getRoleManager().reloadRoleMappings();
            
            MessageUtils.sendMessage(sender, "&aConfiguration reloaded successfully!");
            
        } catch (Exception e) {
            MessageUtils.sendMessage(sender, "&cFailed to reload configuration: " + e.getMessage());
            plugin.getLogger().severe("Error reloading config: " + e.getMessage());
        }
    }
    
    private void handleGenerate(CommandSender sender) {
        MessageUtils.sendMessage(sender, "&eGenerating roles.yml from LuckPerms groups...");
        
        if (plugin.getRoleManager().generateRolesConfig()) {
            MessageUtils.sendMessage(sender, "&a" + ChatColor.BOLD + "✓ roles.yml has been generated successfully!");
            MessageUtils.sendMessage(sender, "");
            MessageUtils.sendMessage(sender, "&e" + ChatColor.BOLD + "Next Steps:");
            MessageUtils.sendMessage(sender, "&7➤ &fOpen &eroles.yml &fin your plugins folder");
            MessageUtils.sendMessage(sender, "&7➤ &fFill in Discord Role IDs for the groups you want to sync");
            MessageUtils.sendMessage(sender, "&7➤ &fTo get Role IDs: Enable Developer Mode in Discord,");
            MessageUtils.sendMessage(sender, "&7   &fright-click roles and select 'Copy ID'");
            MessageUtils.sendMessage(sender, "&7➤ &fRun &e/discraftsync reload &fwhen done");
            MessageUtils.sendMessage(sender, "");
            MessageUtils.sendMessage(sender, "&aRole mappings will automatically sync when users link accounts!");
            
        } else {
            MessageUtils.sendMessage(sender, "&cFailed to generate roles.yml!");
            MessageUtils.sendMessage(sender, "&cCheck console for error details.");
        }
    }
    
    private void handleInfo(CommandSender sender) {
        MessageUtils.sendMessage(sender, "");
        MessageUtils.sendMessage(sender, "&9" + ChatColor.BOLD + "════════ DiscraftSync Status ════════");
        MessageUtils.sendMessage(sender, "");
        
        // Plugin info
        MessageUtils.sendMessage(sender, "&e" + ChatColor.BOLD + "Plugin Information:");
        MessageUtils.sendMessage(sender, "&7➤ &fVersion: &a" + plugin.getDescription().getVersion());
        MessageUtils.sendMessage(sender, "&7➤ &fAuthor: &aHode");
        MessageUtils.sendMessage(sender, "&7➤ &fWebsite: &ahode.lol");
        MessageUtils.sendMessage(sender, "");
        
        // Discord bot status
        MessageUtils.sendMessage(sender, "&e" + ChatColor.BOLD + "Discord Bot:");
        boolean botOnline = plugin.getDiscordBot() != null && plugin.getDiscordBot().isOnline();
        MessageUtils.sendMessage(sender, "&7➤ &fStatus: " + (botOnline ? "&aOnline" : "&cOffline"));
        
        if (botOnline) {
            MessageUtils.sendMessage(sender, "&7➤ &fGuild: &a" + plugin.getConfigManager().getGuildId());
        }
        MessageUtils.sendMessage(sender, "");
        
        // Statistics
        MessageUtils.sendMessage(sender, "&e" + ChatColor.BOLD + "Statistics:");
        MessageUtils.sendMessage(sender, "&7➤ &fLinked Accounts: &a" + plugin.getLinkManager().getTotalLinkedAccounts());
        MessageUtils.sendMessage(sender, "&7➤ &fRole Mappings: &a" + plugin.getRoleManager().getTotalMappings());
        MessageUtils.sendMessage(sender, "");
        
        // Configuration status
        MessageUtils.sendMessage(sender, "&e" + ChatColor.BOLD + "Configuration:");
        MessageUtils.sendMessage(sender, "&7➤ &fServer IP: &a" + plugin.getConfigManager().getServerIp());
        MessageUtils.sendMessage(sender, "&7➤ &fLogging: " + (plugin.getConfigManager().isLoggingEnabled() ? "&aEnabled" : "&cDisabled"));
        MessageUtils.sendMessage(sender, "&7➤ &fSync on Join: " + (plugin.getConfigManager().isSyncOnJoinEnabled() ? "&aEnabled" : "&cDisabled"));
        MessageUtils.sendMessage(sender, "");
        
        MessageUtils.sendMessage(sender, "&9" + ChatColor.BOLD + "═══════════════════════════════════");
    }
    
    private void sendHelpMessage(CommandSender sender) {
        MessageUtils.sendMessage(sender, "");
        MessageUtils.sendMessage(sender, "&9" + ChatColor.BOLD + "════════ DiscraftSync Commands ════════");
        MessageUtils.sendMessage(sender, "");
        MessageUtils.sendMessage(sender, "&e/discraftsync &7- Generate roles.yml from LuckPerms");
        MessageUtils.sendMessage(sender, "&e/discraftsync generate &7- Generate roles.yml from LuckPerms");
        MessageUtils.sendMessage(sender, "&e/discraftsync reload &7- Reload plugin configuration");
        MessageUtils.sendMessage(sender, "&e/discraftsync info &7- Show plugin status and statistics");
        MessageUtils.sendMessage(sender, "&e/discraftsync help &7- Show this help message");
        MessageUtils.sendMessage(sender, "");
        MessageUtils.sendMessage(sender, "&7Aliases: &e/dsync, /sync");
        MessageUtils.sendMessage(sender, "&7Permission: &ediscraftsync.admin");
        MessageUtils.sendMessage(sender, "");
        MessageUtils.sendMessage(sender, "&9" + ChatColor.BOLD + "═══════════════════════════════════════");
    }
}