package com.hode.discraftsync.listeners;

import com.hode.discraftsync.DiscraftSync;
import com.hode.discraftsync.managers.VerificationManager;
import com.hode.discraftsync.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    
    private final DiscraftSync plugin;
    
    /**
     * Java 8 compatible string repeat method
     */
    private static String repeat(String str, int count) {
        if (count <= 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
    
    public PlayerJoinListener(DiscraftSync plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String ign = player.getName();
        
        // Check if player has pending verification code
        if (plugin.getVerificationManager().hasVerificationCode(ign)) {
            VerificationManager.VerificationData data = plugin.getVerificationManager().getVerificationData(ign);
            
            // Send verification reminder
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                MessageUtils.sendRawMessage(player, "");
                MessageUtils.sendRawMessage(player, "&9" + repeat("═", 40));
                MessageUtils.sendRawMessage(player, "&e" + "⚠" + " &6You have a pending Discord verification!");
                MessageUtils.sendRawMessage(player, "");
                MessageUtils.sendRawMessage(player, "&fYour verification code: &a&l" + data.code);
                MessageUtils.sendRawMessage(player, "&fEnter this code in Discord to link your account.");
                MessageUtils.sendRawMessage(player, "");
                long timeLeft = (data.expiryTime - System.currentTimeMillis()) / 1000;
                if (timeLeft > 0) {
                    MessageUtils.sendRawMessage(player, "&7Code expires in: &e" + timeLeft + " seconds");
                } else {
                    MessageUtils.sendRawMessage(player, "&cCode has expired! Request a new one in Discord.");
                    plugin.getVerificationManager().removeVerificationCode(ign);
                }
                MessageUtils.sendRawMessage(player, "&9" + repeat("═", 40));
                MessageUtils.sendRawMessage(player, "");
            }, 20L); // 1 second delay
        }
        
        // Auto-sync roles if enabled and player is linked
        if (plugin.getConfigManager().isSyncOnJoinEnabled()) {
            if (plugin.getLinkManager().isIgnLinked(ign)) {
                String discordId = plugin.getLinkManager().getLinkedDiscordId(ign);
                
                // Trigger role sync via Discord bot
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    if (plugin.getDiscordBot() != null) {
                        plugin.getDiscordBot().syncUserRoles(discordId);
                    }
                });
            }
        }
    }
}