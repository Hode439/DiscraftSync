# DiscraftSync

A Minecraft plugin that links Discord accounts to Minecraft players and automatically syncs roles between platforms.

## What it does

- Players can link their Discord accounts to their Minecraft accounts using verification codes
- Discord roles automatically sync to LuckPerms groups in real-time
- Admins can manage linked accounts and role mappings
- Works with cracked servers (uses IGN instead of UUID)
- Supports Minecraft 1.8.8 with Java 8

## Setup

### Requirements
- Spigot/Paper server running Minecraft 1.8.8
- LuckPerms plugin installed
- Discord bot token
- Java 8

### Installation

1. Download the latest release and put it in your `plugins/` folder
2. Start your server to generate config files
3. Edit `plugins/DiscraftSync/config.yml` with your Discord bot settings:
   ```yaml
   discord:
     token: "YOUR_BOT_TOKEN"
     guild-id: "YOUR_SERVER_ID"
   ```
4. Run `/sync` in-game to generate role mappings
5. Edit `roles.yml` to add Discord role IDs for groups you want to sync
6. Restart your server

### Discord Bot Setup

1. Go to https://discord.com/developers/applications
2. Create a new application and add a bot
3. Copy the bot token to your config
4. Invite the bot with these permissions:
   - Read Messages
   - Send Messages
   - Use Slash Commands
   - Manage Roles
   - View Channels

## Usage

### For Players
- Use `/link <your_minecraft_name>` in Discord to start linking
- Enter the verification code you receive in Minecraft
- Your roles will sync automatically after linking

### For Admins
- `/sync` - Generate role mapping file from LuckPerms groups
- `/sync reload` - Reload configuration
- `/sync info` - Show plugin status
- `/unlink <discord_user>` - Unlink accounts (Discord command)

## Configuration

The plugin creates three main files:

- `config.yml` - Main settings (Discord token, server info, etc.)
- `roles.yml` - Maps Discord roles to LuckPerms groups
- `linkedAccounts.yml` - Stores linked account data

Role syncing happens **instantly** (took me a while to do this btw) when Discord roles change, and logs are sent to your configured Discord channel.

## Building

Requires Maven and Java 8:

```bash
mvn clean package
```

The compiled JAR will be in the `target/` folder.

## Issues

Found a bug? Open an issue on GitHub with details about your setup and what went wrong.


# Note; This plugin is made with the help of AI. I haven't bothered fixing the shitty code it spits out, use with caution.
