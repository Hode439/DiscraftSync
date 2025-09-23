package com.hode.discraftsync.listeners;

import com.hode.discraftsync.DiscraftSync;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

public class DiscordListener extends ListenerAdapter {
    
    private final DiscraftSync plugin;
    
    public DiscordListener(DiscraftSync plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        handleRoleChange(event.getUser().getId());
    }
    
    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        handleRoleChange(event.getUser().getId());
    }
    
    private void handleRoleChange(String discordId) {
        // Check if this user has a linked account
        String ign = plugin.getLinkManager().getLinkedIgn(discordId);
        if (ign == null) {
            return; // Not linked, ignore
        }
        
        // Sync roles immediately on main thread (async to avoid blocking Discord)
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // Small delay to ensure Discord API is updated
                Thread.sleep(500);
                
                // Trigger role sync
                plugin.getDiscordBot().syncUserRoles(discordId);
                
                plugin.getLogger().info("Auto-synced roles for " + ign + " due to Discord role change");
                
            } catch (Exception e) {
                plugin.getLogger().warning("Error auto-syncing roles for " + ign + ": " + e.getMessage());
            }
        });
    }
}