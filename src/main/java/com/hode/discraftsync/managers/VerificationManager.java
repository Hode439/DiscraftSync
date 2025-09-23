package com.hode.discraftsync.managers;

import com.hode.discraftsync.DiscraftSync;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class VerificationManager {
    
    private final DiscraftSync plugin;
    private final ConfigManager configManager;
    
    // Verification codes storage (IGN -> VerificationData)
    private final Map<String, VerificationData> verificationCodes = new ConcurrentHashMap<>();
    
    // Attempt tracking (Discord ID -> attempt count)
    private final Map<String, Integer> attemptCounts = new ConcurrentHashMap<>();
    
    private final Random random = new Random();
    
    public VerificationManager(DiscraftSync plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        
        // Start cleanup task
        startCleanupTask();
    }
    
    public String generateVerificationCode(String discordId, String ign) {
        // Check if user has exceeded max attempts
        int attempts = attemptCounts.getOrDefault(discordId, 0);
        if (attempts >= configManager.getMaxVerificationAttempts()) {
            return null; // Max attempts exceeded
        }
        
        // Generate code
        String code = createRandomCode();
        long expiryTime = System.currentTimeMillis() + (configManager.getCodeExpirySeconds() * 1000L);
        
        // Store verification data
        VerificationData data = new VerificationData(discordId, ign, code, expiryTime);
        verificationCodes.put(ign.toLowerCase(), data);
        
        plugin.getLogger().info("Generated verification code for " + ign + " (Discord: " + discordId + ")");
        return code;
    }
    
    public VerificationResult verifyCode(String ign, String inputCode) {
        VerificationData data = verificationCodes.get(ign.toLowerCase());
        
        if (data == null) {
            return VerificationResult.NO_CODE;
        }
        
        // Check if expired
        if (System.currentTimeMillis() > data.expiryTime) {
            verificationCodes.remove(ign.toLowerCase());
            return VerificationResult.EXPIRED;
        }
        
        // Check if code matches
        if (!data.code.equalsIgnoreCase(inputCode)) {
            // Increment attempt count
            attemptCounts.put(data.discordId, attemptCounts.getOrDefault(data.discordId, 0) + 1);
            return VerificationResult.WRONG_CODE;
        }
        
        // Success - clean up
        verificationCodes.remove(ign.toLowerCase());
        attemptCounts.remove(data.discordId);
        
        plugin.getLogger().info("Successfully verified code for " + ign + " (Discord: " + data.discordId + ")");
        return VerificationResult.SUCCESS;
    }
    
    public VerificationData getVerificationData(String ign) {
        return verificationCodes.get(ign.toLowerCase());
    }
    
    public Set<String> getAllPendingVerifications() {
        return verificationCodes.keySet();
    }
    
    public boolean hasVerificationCode(String ign) {
        VerificationData data = verificationCodes.get(ign.toLowerCase());
        if (data == null) {
            return false;
        }
        
        // Check if expired
        if (System.currentTimeMillis() > data.expiryTime) {
            verificationCodes.remove(ign.toLowerCase());
            return false;
        }
        
        return true;
    }
    
    public void removeVerificationCode(String ign) {
        VerificationData data = verificationCodes.remove(ign.toLowerCase());
        if (data != null) {
            attemptCounts.remove(data.discordId);
        }
    }
    
    public int getRemainingAttempts(String discordId) {
        int attempts = attemptCounts.getOrDefault(discordId, 0);
        return Math.max(0, configManager.getMaxVerificationAttempts() - attempts);
    }
    
    public void clearAttempts(String discordId) {
        attemptCounts.remove(discordId);
    }
    
    public int getTotalPendingVerifications() {
        return verificationCodes.size();
    }
    
    public void clearExpiredCodes() {
        long currentTime = System.currentTimeMillis();
        verificationCodes.entrySet().removeIf(entry -> {
            if (currentTime > entry.getValue().expiryTime) {
                attemptCounts.remove(entry.getValue().discordId);
                return true;
            }
            return false;
        });
    }
    
    private String createRandomCode() {
        StringBuilder code = new StringBuilder();
        int length = configManager.getCodeLength();
        boolean useUppercase = configManager.useUppercase();
        boolean useNumbers = configManager.useNumbers();
        
        String chars = "";
        if (useUppercase) {
            chars += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        }
        if (useNumbers) {
            chars += "0123456789";
        }
        
        if (chars.isEmpty()) {
            chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // Fallback
        }
        
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return code.toString();
    }
    
    private void startCleanupTask() {
        // Run cleanup every 60 seconds
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            clearExpiredCodes();
            
            if (configManager.isDebugEnabled()) {
                plugin.getLogger().info("Cleaned up expired verification codes. " +
                    "Remaining: " + verificationCodes.size());
            }
            
        }, 1200L, 1200L); // 60 seconds in ticks (20 ticks = 1 second)
    }
    
    public static class VerificationData {
        public final String discordId;
        public final String ign;
        public final String code;
        public final long expiryTime;
        
        public VerificationData(String discordId, String ign, String code, long expiryTime) {
            this.discordId = discordId;
            this.ign = ign;
            this.code = code;
            this.expiryTime = expiryTime;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
        
        public long getTimeUntilExpiry() {
            return Math.max(0, expiryTime - System.currentTimeMillis());
        }
        
        public int getSecondsUntilExpiry() {
            return (int) (getTimeUntilExpiry() / 1000);
        }
    }
}