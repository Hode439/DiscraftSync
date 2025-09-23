package com.hode.discraftsync.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.Instant;

public class EmbedUtils {
    
    // Colors
    public static final int SUCCESS_COLOR = 0x00ff00; // Green
    public static final int ERROR_COLOR = 0xff0000;   // Red
    public static final int INFO_COLOR = 0x0099ff;    // Blue
    public static final int WARNING_COLOR = 0xff9900; // Orange
    
    // Emojis
    public static final String SUCCESS_EMOJI = "✅";
    public static final String ERROR_EMOJI = "❌";
    public static final String INFO_EMOJI = "ℹ️";
    public static final String WARNING_EMOJI = "⚠️";
    public static final String LINK_EMOJI = "🔗";
    public static final String SYNC_EMOJI = "🔄";
    
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
    
    public static MessageEmbed createSuccessEmbed(String title, String description) {
        return new EmbedBuilder()
            .setColor(SUCCESS_COLOR)
            .setTitle("═══💠 Discraft Sync 💠═══")
            .addField(SUCCESS_EMOJI + " " + title, description, false)
            .addField("", repeat("═", 30), false)
            .setTimestamp(Instant.now())
            .setFooter("DiscraftSync by Hode", null)
            .build();
    }
    
    public static MessageEmbed createErrorEmbed(String title, String description) {
        return new EmbedBuilder()
            .setColor(ERROR_COLOR)
            .setTitle("═══💠 Discraft Sync 💠═══")
            .addField(ERROR_EMOJI + " " + title, description, false)
            .addField("", repeat("═", 30), false)
            .setTimestamp(Instant.now())
            .setFooter("DiscraftSync by Hode", null)
            .build();
    }
    
    public static MessageEmbed createInfoEmbed(String title, String description) {
        return new EmbedBuilder()
            .setColor(INFO_COLOR)
            .setTitle("═══💠 Discraft Sync 💠═══")
            .addField(INFO_EMOJI + " " + title, description, false)
            .addField("", repeat("═", 30), false)
            .setTimestamp(Instant.now())
            .setFooter("DiscraftSync by Hode", null)
            .build();
    }
    
    public static MessageEmbed createWarningEmbed(String title, String description) {
        return new EmbedBuilder()
            .setColor(WARNING_COLOR)
            .setTitle("═══💠 Discraft Sync 💠═══")
            .addField(WARNING_EMOJI + " " + title, description, false)
            .addField("", repeat("═", 30), false)
            .setTimestamp(Instant.now())
            .setFooter("DiscraftSync by Hode", null)
            .build();
    }
    
    public static MessageEmbed createLogEmbed(String title, String description, int color) {
        return new EmbedBuilder()
            .setColor(color)
            .setTitle(SYNC_EMOJI + " DiscraftSync Log")
            .addField(title, description, false)
            .setTimestamp(Instant.now())
            .setFooter("Hode.lol", null)
            .build();
    }
    
    public static MessageEmbed createLinkingEmbed(String ign, String code, int expirySeconds) {
        return new EmbedBuilder()
            .setColor(INFO_COLOR)
            .setTitle("═══💠 Discraft Sync 💠═══")
            .addField(LINK_EMOJI + " Link Request", 
                "A verification code has been sent to **" + ign + "** in-game.\n\n" +
                "**Your Code:** `" + code + "`\n" +
                "**Expires in:** " + expirySeconds + " seconds\n\n" +
                "Enter this code in Discord to complete linking.", false)
            .addField("", repeat("═", 30), false)
            .setTimestamp(Instant.now())
            .setFooter("DiscraftSync by Hode • hode.lol", null)
            .build();
    }
    
    public static MessageEmbed createPlayerOfflineEmbed(String serverIp) {
        return new EmbedBuilder()
            .setColor(ERROR_COLOR)
            .setTitle("═══💠 Discraft Sync 💠═══")
            .addField(ERROR_EMOJI + " Player Not Online!", 
                "Please join the server first:\n**" + serverIp + "**\n\n" +
                "You must be online to verify your account.", false)
            .addField("", repeat("═", 30), false)
            .setTimestamp(Instant.now())
            .setFooter("DiscraftSync by Hode • hode.lol", null)
            .build();
    }
    
    public static MessageEmbed createAlreadyLinkedEmbed(String currentIgn) {
        return new EmbedBuilder()
            .setColor(ERROR_COLOR)
            .setTitle("═══💠 Discraft Sync 💠═══")
            .addField(ERROR_EMOJI + " Already Linked!", 
                "Your Discord account is already linked to:\n**" + currentIgn + "**\n\n" +
                "Contact an admin if you need to change this.", false)
            .addField("", repeat("═", 30), false)
            .setTimestamp(Instant.now())
            .setFooter("DiscraftSync by Hode • hode.lol", null)
            .build();
    }
    
    public static MessageEmbed createIgnAlreadyLinkedEmbed(String ign) {
        return new EmbedBuilder()
            .setColor(ERROR_COLOR)
            .setTitle("═══💠 Discraft Sync 💠═══")
            .addField(ERROR_EMOJI + " IGN Already Linked!", 
                "The Minecraft account **" + ign + "** is already linked to another Discord account.\n\n" +
                "Contact an admin for assistance.", false)
            .addField("", repeat("═", 30), false)
            .setTimestamp(Instant.now())
            .setFooter("DiscraftSync by Hode • hode.lol", null)
            .build();
    }
}