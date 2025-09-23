package com.hode.discraftsync.discord;

import com.hode.discraftsync.DiscraftSync;
import com.hode.discraftsync.managers.VerificationManager;
import com.hode.discraftsync.managers.VerificationResult;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SlashCommands extends ListenerAdapter {
    
    private final DiscraftSync plugin;
    private final DiscordBot discordBot;
    
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
    
    public SlashCommands(DiscraftSync plugin, DiscordBot discordBot) {
        this.plugin = plugin;
        this.discordBot = discordBot;
        
        // Register this as event listener
        discordBot.getJDA().addEventListener(this);
        
        // Register slash commands
        registerCommands();
    }
    
    private void registerCommands() {
        discordBot.getGuild().updateCommands().addCommands(
            Commands.slash("link", "Link your Minecraft account to Discord")
                .addOption(OptionType.STRING, "ign", "Your Minecraft username", true),
                
            Commands.slash("unlink", "Unlink Discord and Minecraft accounts (Admin only)")
                .addOption(OptionType.USER, "discord", "Discord user to unlink", false)
                .addOption(OptionType.STRING, "ign", "Minecraft username to unlink", false)
        ).queue();
    }
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        
        switch (command) {
            case "link":
                handleLinkCommand(event);
                break;
                
            case "unlink":
                handleUnlinkCommand(event);
                break;
        }
    }
    
    private void handleLinkCommand(SlashCommandInteractionEvent event) {
        String discordId = event.getUser().getId();
        String ign = event.getOption("ign").getAsString();
        
        // Check if already linked
        if (plugin.getLinkManager().isDiscordLinked(discordId)) {
            event.replyEmbeds(EmbedUtils.createErrorEmbed(
                "Already Linked!",
                "Your Discord account is already linked to a Minecraft account.\nContact an admin if you need to change it."
            )).setEphemeral(true).queue();
            return;
        }
        
        if (plugin.getLinkManager().isIgnLinked(ign)) {
            event.replyEmbeds(EmbedUtils.createErrorEmbed(
                "IGN Already Linked!",
                "The Minecraft account **" + ign + "** is already linked to another Discord account.\nContact an admin for assistance."
            )).setEphemeral(true).queue();
            return;
        }
        
        // Check if player is online
        Player player = Bukkit.getPlayerExact(ign);
        if (player == null) {
            event.replyEmbeds(EmbedUtils.createErrorEmbed(
                "Player Not Online!",
                "Please join the server first: **" + plugin.getConfigManager().getServerIp() + "**"
            )).setEphemeral(true).queue();
            return;
        }
        
        // Generate verification code
        String code = plugin.getVerificationManager().generateVerificationCode(discordId, ign);
        if (code == null) {
            int remaining = plugin.getVerificationManager().getRemainingAttempts(discordId);
            event.replyEmbeds(EmbedUtils.createErrorEmbed(
                "Too Many Attempts!",
                "You have exceeded the maximum number of verification attempts.\nPlease wait before trying again."
            )).setEphemeral(true).queue();
            return;
        }
        
        // Send code to player in-game
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.sendMessage("§9" + repeat("═", 40));
            player.sendMessage("§e⚠ §6Discord Account Linking");
            player.sendMessage("");
            player.sendMessage("§fSomeone is trying to link your account to Discord.");
            player.sendMessage("§fYour verification code: §a§l" + code);
            player.sendMessage("");
            player.sendMessage("§7Enter this code in Discord to complete linking.");
            player.sendMessage("§7Code expires in " + plugin.getConfigManager().getCodeExpirySeconds() + " seconds.");
            player.sendMessage("§9" + repeat("═", 40));
        });
        
        // Reply with verification embed
        event.replyEmbeds(EmbedUtils.createInfoEmbed(
            "Verification Code Sent!",
            "A verification code has been sent to **" + ign + "** in-game.\n" +
            "Click the button below to enter your code."
        )).addComponents(
            ActionRow.of(Button.primary("verify_" + discordId, "Enter Verification Code"))
        ).setEphemeral(true).queue();
    }
    
    private void handleUnlinkCommand(SlashCommandInteractionEvent event) {
        // Check admin permissions
        String adminRoleId = plugin.getConfigManager().getAdminRoleId();
        Member member = event.getMember();
        
        if (member == null || !member.getRoles().stream().anyMatch(role -> role.getId().equals(adminRoleId))) {
            event.replyEmbeds(EmbedUtils.createErrorEmbed(
                "Permission Denied!",
                "You don't have permission to use this command."
            )).setEphemeral(true).queue();
            return;
        }
        
        User discordUser = event.getOption("discord") != null ? event.getOption("discord").getAsUser() : null;
        String ign = event.getOption("ign") != null ? event.getOption("ign").getAsString() : null;
        
        if (discordUser == null && ign == null) {
            event.replyEmbeds(EmbedUtils.createErrorEmbed(
                "Missing Arguments!",
                "Please specify either a Discord user or Minecraft IGN to unlink."
            )).setEphemeral(true).queue();
            return;
        }
        
        boolean success = false;
        String unlinkedInfo = "";
        
        if (discordUser != null) {
            String linkedIgn = plugin.getLinkManager().getLinkedIgn(discordUser.getId());
            if (linkedIgn != null) {
                success = plugin.getLinkManager().unlinkAccount(discordUser.getId());
                unlinkedInfo = "Discord: " + discordUser.getAsMention() + " (was linked to **" + linkedIgn + "**)";
            }
        } else if (ign != null) {
            String linkedDiscordId = plugin.getLinkManager().getLinkedDiscordId(ign);
            if (linkedDiscordId != null) {
                success = plugin.getLinkManager().unlinkAccountByIgn(ign);
                User linkedUser = discordBot.getJDA().getUserById(linkedDiscordId);
                unlinkedInfo = "IGN: **" + ign + "** (was linked to " + 
                    (linkedUser != null ? linkedUser.getAsMention() : "Unknown User") + ")";
            }
        }
        
        if (success) {
            event.replyEmbeds(EmbedUtils.createSuccessEmbed(
                "Account Unlinked!",
                unlinkedInfo + "\n\nThe account linking has been removed successfully."
            )).queue();
            
            // Log the event
            discordBot.logEvent(
                "Account Unlinked",
                "**Admin:** " + event.getUser().getAsMention() + "\n" +
                "**Unlinked:** " + unlinkedInfo,
                0xff9900 // Orange color
            );
            
        } else {
            event.replyEmbeds(EmbedUtils.createErrorEmbed(
                "Unlink Failed!",
                "Could not find any linked account for the specified user/IGN."
            )).setEphemeral(true).queue();
        }
    }
    
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        
        if (buttonId.startsWith("verify_")) {
            String discordId = buttonId.substring(7);
            
            if (!event.getUser().getId().equals(discordId)) {
                event.reply("This verification is not for you!").setEphemeral(true).queue();
                return;
            }
            
            // Create verification modal
            TextInput codeInput = TextInput.create("verification_code", "Verification Code", TextInputStyle.SHORT)
                .setPlaceholder("Enter the code from Minecraft")
                .setRequiredRange(plugin.getConfigManager().getCodeLength(), plugin.getConfigManager().getCodeLength())
                .build();
            
            Modal modal = Modal.create("verify_modal_" + discordId, "Enter Verification Code")
                .addComponents(ActionRow.of(codeInput))
                .build();
            
            event.replyModal(modal).queue();
        }
    }
    
    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        String modalId = event.getModalId();
        
        if (modalId.startsWith("verify_modal_")) {
            String discordId = modalId.substring(13);
            String inputCode = event.getValue("verification_code").getAsString();
            
            // Find verification data
            VerificationManager.VerificationData data = null;
            for (String ign : plugin.getVerificationManager().getAllPendingVerifications()) {
                VerificationManager.VerificationData d = plugin.getVerificationManager().getVerificationData(ign);
                if (d != null && d.discordId.equals(discordId)) {
                    data = d;
                    break;
                }
            }
            
            if (data == null) {
                event.replyEmbeds(EmbedUtils.createErrorEmbed(
                    "No Verification Found!",
                    "No pending verification found. Please run `/link` again."
                )).setEphemeral(true).queue();
                return;
            }
            
            // Verify code
            VerificationResult result = plugin.getVerificationManager().verifyCode(data.ign, inputCode);
            
            switch (result) {
                case SUCCESS:
                    handleSuccessfulVerification(event, discordId, data.ign);
                    break;
                    
                case WRONG_CODE:
                    int remaining = plugin.getVerificationManager().getRemainingAttempts(discordId);
                    event.replyEmbeds(EmbedUtils.createErrorEmbed(
                        "Incorrect Code!",
                        "The verification code is incorrect.\n" +
                        "Remaining attempts: **" + remaining + "**"
                    )).setEphemeral(true).queue();
                    break;
                    
                case EXPIRED:
                    event.replyEmbeds(EmbedUtils.createErrorEmbed(
                        "Code Expired!",
                        "Your verification code has expired.\nPlease run `/link` again to get a new code."
                    )).setEphemeral(true).queue();
                    break;
                    
                case NO_CODE:
                    event.replyEmbeds(EmbedUtils.createErrorEmbed(
                        "No Code Found!",
                        "No verification code found.\nPlease run `/link` again."
                    )).setEphemeral(true).queue();
                    break;
            }
        }
    }
    
    private void handleSuccessfulVerification(ModalInteractionEvent event, String discordId, String ign) {
        // Link the accounts
        if (plugin.getLinkManager().linkAccount(discordId, ign)) {
            
            // Assign default Discord role
            discordBot.assignDefaultRole(discordId);
            
            // Assign default LuckPerms group
            Bukkit.getScheduler().runTask(plugin, () -> {
                plugin.getRoleManager().syncUserRoles(ign, java.util.Arrays.asList(
                    plugin.getConfigManager().getDefaultDiscordRole()
                ));
            });
            
            // Send success message
            event.replyEmbeds(EmbedUtils.createSuccessEmbed(
                "Account Linked Successfully!",
                "🎉 **Congratulations!** Your accounts have been linked!\n\n" +
                "**Discord:** " + event.getUser().getAsMention() + "\n" +
                "**Minecraft:** **" + ign + "**\n" +
                "**Role:** " + plugin.getConfigManager().getDefaultLuckPermsGroup() + "\n\n" +
                "Your roles will now automatically sync between Discord and Minecraft!"
            )).queue();
            
            // Send confirmation to player in-game
            Player player = Bukkit.getPlayerExact(ign);
            if (player != null) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.sendMessage("§9" + repeat("═", 40));
                    player.sendMessage("§a✓ §2Account Linked Successfully!");
                    player.sendMessage("");
                    player.sendMessage("§fYour Discord and Minecraft accounts are now linked.");
                    player.sendMessage("§fRoles will automatically sync between platforms.");
                    player.sendMessage("§9" + repeat("═", 40));
                });
            }
            
            // Log the linking event
            discordBot.logEvent(
                "Account Linked",
                "**Discord:** " + event.getUser().getAsMention() + "\n" +
                "**Minecraft:** **" + ign + "**\n" +
                "**Group:** " + plugin.getConfigManager().getDefaultLuckPermsGroup(),
                0x00ff00 // Green color
            );
            
        } else {
            event.replyEmbeds(EmbedUtils.createErrorEmbed(
                "Linking Failed!",
                "Failed to link accounts. The account might already be linked.\nContact an admin for assistance."
            )).setEphemeral(true).queue();
        }
    }
}