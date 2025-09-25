package org.gi.gICore.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.gi.gICore.CoreLogger;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

public class CoreConfig {
    private final FileConfiguration config;
    private final File configFile;

    public CoreConfig(File file){
        this.configFile = file;
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public CoreConfig(FileConfiguration config, File configFile) {
        this.config = config;
        this.configFile = configFile;
    }

    public Set<String> getKeys(boolean ignoreCase){
        return config.getKeys(ignoreCase);
    }

    public boolean isString(Object key){
        return key instanceof String;
    }

    public String getString(String key){
        return config.getString(key);
    }

    public int getInt(String key){
        return config.getInt(key);
    }

    public boolean getBoolean(String key){
        return config.getBoolean(key);
    }

    public double getDouble(String key){
        return config.getDouble(key);
    }

    public Long getLong(String key){
        return config.getLong(key);
    }

    public List<String> getStringList(String key){
        return config.getStringList(key);
    }

    public List<Integer> getIntList(String key){
        return config.getIntegerList(key);
    }

    public List<Boolean> getBooleanList(String key){
        return config.getBooleanList(key);
    }

    public List<Double> getDoubleList(String key){
        return config.getDoubleList(key);
    }

    public List<Long> getLongList(String key){
        return config.getLongList(key);
    }
    public <T> T getValue(String fileName, String path, Class<T> type, T defaultValue) {
        if (config == null) {
            return defaultValue;
        }

        Object value = config.get(path);
        if (value == null || !type.isInstance(value)) {
            return defaultValue;
        }

        return type.cast(value);
    }
    public ConfigurationSection getConfigurationSection(String key){
        return config.getConfigurationSection(key);
    }
}
