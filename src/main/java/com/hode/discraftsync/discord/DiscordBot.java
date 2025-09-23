package com.hode.discraftsync.discord;

import com.hode.discraftsync.DiscraftSync;
import com.hode.discraftsync.discord.SlashCommands;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.stream.Collectors;

public class DiscordBot {
    
    private final DiscraftSync plugin;
    private JDA jda;
    private Guild guild;
    private SlashCommands slashCommands;
    
    public DiscordBot(DiscraftSync plugin) {
        this.plugin = plugin;
    }
    
    public boolean initialize() {
        try {
            String token = plugin.getConfigManager().getDiscordToken();
            
            // Build JDA
            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                    .build();
            
            // Wait for JDA to be ready
            jda.awaitReady();
            
            // Get guild
            String guildId = plugin.getConfigManager().getGuildId();
            guild = jda.getGuildById(guildId);
            
            if (guild == null) {
                plugin.getLogger().severe("Could not find Discord guild with ID: " + guildId);
                return false;
            }
            
            // Initialize slash commands
            slashCommands = new SlashCommands(plugin, this);
            
            // Register Discord event listener for real-time role changes
            jda.addEventListener(new com.hode.discraftsync.listeners.DiscordListener(plugin));
            
            plugin.getLogger().info("Discord bot connected successfully to: " + guild.getName());
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize Discord bot: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public void syncUserRoles(String discordId) {
        if (guild == null) return;
        
        try {
            Member member = guild.getMemberById(discordId);
            if (member == null) {
                plugin.getLogger().warning("Could not find member with ID: " + discordId);
                return;
            }
            
            // Get user's Discord roles
            List<String> roleIds = member.getRoles().stream()
                    .map(Role::getId)
                    .collect(Collectors.toList());
            
            // Get linked IGN
            String ign = plugin.getLinkManager().getLinkedIgn(discordId);
            if (ign == null) {
                plugin.getLogger().warning("No linked IGN found for Discord ID: " + discordId);
                return;
            }
            
            // Sync roles on main thread
            Bukkit.getScheduler().runTask(plugin, () -> {
                plugin.getRoleManager().syncUserRoles(ign, roleIds);
            });
            
        } catch (Exception e) {
            plugin.getLogger().warning("Error syncing roles for " + discordId + ": " + e.getMessage());
        }
    }
    
    public void assignDefaultRole(String discordId) {
        if (guild == null) return;
        
        String defaultRoleId = plugin.getConfigManager().getDefaultDiscordRole();
        if (defaultRoleId.isEmpty()) return;
        
        try {
            Member member = guild.getMemberById(discordId);
            Role defaultRole = guild.getRoleById(defaultRoleId);
            
            if (member != null && defaultRole != null) {
                guild.addRoleToMember(member, defaultRole).queue(
                    success -> plugin.getLogger().info("Assigned default role to " + member.getEffectiveName()),
                    error -> plugin.getLogger().warning("Failed to assign default role: " + error.getMessage())
                );
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error assigning default role: " + e.getMessage());
        }
    }
    
    public boolean isUserInGuild(String discordId) {
        if (guild == null) return false;
        
        try {
            Member member = guild.getMemberById(discordId);
            return member != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getUserDisplayName(String discordId) {
        if (guild == null) return "Unknown User";
        
        try {
            Member member = guild.getMemberById(discordId);
            return member != null ? member.getEffectiveName() : "Unknown User";
        } catch (Exception e) {
            return "Unknown User";
        }
    }
    
    public void logEvent(String title, String description, int color) {
        String channelId = plugin.getConfigManager().getLoggingChannelId();
        if (channelId.isEmpty() || !plugin.getConfigManager().isLoggingEnabled()) {
            return;
        }
        
        try {
            guild.getTextChannelById(channelId).sendMessageEmbeds(
                EmbedUtils.createLogEmbed(title, description, color)
            ).queue();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to log event to Discord: " + e.getMessage());
        }
    }
    
    public void shutdown() {
        if (jda != null) {
            jda.shutdown();
            plugin.getLogger().info("Discord bot has been shut down.");
        }
    }
    
    public boolean isOnline() {
        return jda != null && jda.getStatus() == JDA.Status.CONNECTED;
    }
    
    public JDA getJDA() {
        return jda;
    }
    
    public Guild getGuild() {
        return guild;
    }
    
    public SlashCommands getSlashCommands() {
        return slashCommands;
    }
}