package com.hode.discraftsync.utils;

import com.hode.discraftsync.DiscraftSync;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtils {
    
    private static DiscraftSync plugin = DiscraftSync.getInstance();
    
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
    
    /**
     * Send a colored message to a command sender
     */
    public static void sendMessage(CommandSender sender, String message) {
        if (plugin != null && plugin.getConfigManager().isColoredChatEnabled()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getConfigManager().getPrefix() + message));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getConfigManager().getPrefix() + message));
        }
    }
    
    /**
     * Send a message without prefix
     */
    public static void sendRawMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
    
    /**
     * Send a success message
     */
    public static void sendSuccess(CommandSender sender, String message) {
        sendMessage(sender, "&a✓ &2" + message);
    }
    
    /**
     * Send an error message
     */
    public static void sendError(CommandSender sender, String message) {
        sendMessage(sender, "&c✗ &4" + message);
    }
    
    /**
     * Send an info message
     */
    public static void sendInfo(CommandSender sender, String message) {
        sendMessage(sender, "&bℹ &3" + message);
    }
    
    /**
     * Send a warning message
     */
    public static void sendWarning(CommandSender sender, String message) {
        sendMessage(sender, "&e⚠ &6" + message);
    }
    
    /**
     * Send a formatted header
     */
    public static void sendHeader(CommandSender sender, String title) {
        sendRawMessage(sender, "");
        sendRawMessage(sender, "&9" + repeat("═", 20) + " &b" + title + " &9" + repeat("═", 20));
        sendRawMessage(sender, "");
    }
    
    /**
     * Send a formatted footer
     */
    public static void sendFooter(CommandSender sender) {
        sendRawMessage(sender, "");
        sendRawMessage(sender, "&9" + repeat("═", 50));
        sendRawMessage(sender, "");
    }
    
    /**
     * Send verification code display to player
     */
    public static void sendVerificationCode(Player player, String code, int expirySeconds) {
        sendRawMessage(player, "");
        sendRawMessage(player, "&9&l" + repeat("═", 45));
        sendRawMessage(player, "&e&l⚠ &6&lDISCORD ACCOUNT LINKING");
        sendRawMessage(player, "");
        sendRawMessage(player, "&fSomeone is trying to link your account to Discord.");
        sendRawMessage(player, "");
        sendRawMessage(player, "&7Your verification code:");
        sendRawMessage(player, "&a&l&n" + code);
        sendRawMessage(player, "");
        sendRawMessage(player, "&7• Enter this code in Discord to complete linking");
        sendRawMessage(player, "&7• Code expires in &e&l" + expirySeconds + " &7seconds");
        sendRawMessage(player, "&7• If you didn't request this, ignore it");
        sendRawMessage(player, "&9&l" + repeat("═", 45));
        sendRawMessage(player, "");
    }
    
    /**
     * Send linking success message to player
     */
    public static void sendLinkingSuccess(Player player, String discordUser) {
        sendRawMessage(player, "");
        sendRawMessage(player, "&9&l" + repeat("═", 40));
        sendRawMessage(player, "&a&l✓ &2&lACCOUNT LINKED SUCCESSFULLY!");
        sendRawMessage(player, "");
        sendRawMessage(player, "&fYour account has been linked to:");
        sendRawMessage(player, "&b&l" + discordUser);
        sendRawMessage(player, "");
        sendRawMessage(player, "&7• &aRoles will sync automatically");
        sendRawMessage(player, "&7• &aPermissions updated in real-time");
        sendRawMessage(player, "&7• &aEnjoy the server!");
        sendRawMessage(player, "&9&l" + repeat("═", 40));
        sendRawMessage(player, "");
    }
    
    /**
     * Send role sync notification to player
     */
    public static void sendRoleSync(Player player, String oldGroup, String newGroup) {
        sendRawMessage(player, "");
        sendRawMessage(player, "&9&l" + repeat("═", 35));
        sendRawMessage(player, "&e&l🔄 &6&lROLE SYNCHRONIZED");
        sendRawMessage(player, "");
        sendRawMessage(player, "&7Your Discord roles have been synced:");
        sendRawMessage(player, "&c&l" + oldGroup + " &7➤ &a&l" + newGroup);
        sendRawMessage(player, "");
        sendRawMessage(player, "&7Permissions updated automatically!");
        sendRawMessage(player, "&9&l" + repeat("═", 35));
        sendRawMessage(player, "");
    }
    
    /**
     * Color a string without sending it
     */
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    /**
     * Strip color codes from a string
     */
    public static String stripColor(String message) {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message));
    }
    
    /**
     * Send a centered message
     */
    public static void sendCenteredMessage(CommandSender sender, String message) {
        int maxWidth = 53; // Approximate chat width
        int messageLength = stripColor(message).length();
        int padding = (maxWidth - messageLength) / 2;
        
        String centeredMessage = repeat(" ", Math.max(0, padding)) + message;
        sendRawMessage(sender, centeredMessage);
    }
    
    /**
     * Create a progress bar
     */
    public static String createProgressBar(int current, int max, int length, char progressChar, char emptyChar) {
        double percentage = (double) current / max;
        int progress = (int) (percentage * length);
        
        StringBuilder bar = new StringBuilder("&a");
        for (int i = 0; i < length; i++) {
            if (i < progress) {
                bar.append(progressChar);
            } else {
                bar.append("&7").append(emptyChar);
            }
        }
        
        return bar.toString();
    }
}