package org.gi.gICore.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gICore.GICore;
import org.gi.gICore.util.CoreConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    public static ConfigManager instance;
    private final JavaPlugin plugin;

    private final Map<String, CoreConfig> configs = new HashMap<String, CoreConfig>();
    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public CoreConfig loadConfig(String path) {
        File file = new File(plugin.getDataFolder(), path);
        if (!file.exists()) {
            plugin.getResource(path);
        }
        CoreConfig config = new CoreConfig(file);
        return config;
    }

    public CoreConfig getConfig(String path) {
        if (!configs.containsKey(path)) {
            return null;
        }
        return configs.get(path);
    }



}
