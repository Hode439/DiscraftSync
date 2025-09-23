package com.hode.discraftsync.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    
    /**
     * Create a backup of a file
     */
    public static boolean createBackup(File originalFile, File backupDir) {
        if (!originalFile.exists()) {
            return false;
        }
        
        try {
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String backupName = originalFile.getName().replace(".yml", "_" + timestamp + ".yml");
            File backupFile = new File(backupDir, backupName);
            
            Files.copy(originalFile.toPath(), backupFile.toPath());
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Write lines to a file
     */
    public static boolean writeLinesToFile(File file, List<String> lines) {
        try {
            Path path = file.toPath();
            Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Read lines from a file
     */
    public static List<String> readLinesFromFile(File file) {
        try {
            return Files.readAllLines(file.toPath());
            
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Append a line to a file
     */
    public static boolean appendLineToFile(File file, String line) {
        try {
            Files.write(file.toPath(), (line + System.lineSeparator()).getBytes(), 
                       StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Create a directory if it doesn't exist
     */
    public static boolean createDirectory(File directory) {
        if (!directory.exists()) {
            return directory.mkdirs();
        }
        return true;
    }
    
    /**
     * Get file size in human-readable format
     */
    public static String getHumanReadableSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    /**
     * Get file extension
     */
    public static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // Empty extension
        }
        return name.substring(lastIndexOf + 1);
    }
    
    /**
     * Copy a file
     */
    public static boolean copyFile(File source, File destination) {
        try {
            // Create parent directories if they don't exist
            File parentDir = destination.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            Files.copy(source.toPath(), destination.toPath());
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete a file or directory recursively
     */
    public static boolean deleteRecursively(File file) {
        if (!file.exists()) {
            return true;
        }
        
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    if (!deleteRecursively(child)) {
                        return false;
                    }
                }
            }
        }
        
        return file.delete();
    }
    
    /**
     * Load a YAML configuration from file
     */
    public static FileConfiguration loadYamlConfig(File file) {
        if (!file.exists()) {
            return null;
        }
        
        try {
            return YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Save a YAML configuration to file
     */
    public static boolean saveYamlConfig(FileConfiguration config, File file) {
        try {
            // Create parent directories if they don't exist
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            config.save(file);
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Check if a file is writable
     */
    public static boolean isWritable(File file) {
        if (file.exists()) {
            return file.canWrite();
        } else {
            // Check if parent directory is writable
            File parent = file.getParentFile();
            return parent != null && parent.canWrite();
        }
    }
    
    /**
     * Get the last modified time of a file as a formatted string
     */
    public static String getLastModified(File file) {
        if (!file.exists()) {
            return "Never";
        }
        
        long lastModified = file.lastModified();
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(lastModified / 1000, 0, 
                                 java.time.ZoneOffset.systemDefault().getRules().getOffset(java.time.Instant.now()));
        
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}