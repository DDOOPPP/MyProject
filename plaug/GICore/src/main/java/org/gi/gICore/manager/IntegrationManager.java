package org.gi.gICore.manager;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gICore.manager.integrations.PluginIntegration;
import org.gi.gICore.util.ModuleLogger;

import java.util.HashMap;
import java.util.Map;

public class IntegrationManager {
    private final JavaPlugin plugin;
    private final ModuleLogger logger;
    private final Map<String, PluginIntegration> integrations;

    private boolean vaultEnabled = false;
    private boolean mmoItemsEnabled = false;
    private boolean mmoCoreEnabled = false;
    private boolean placeholderAPIEnabled = false;

    // 서비스 인스턴스
    private Economy economy;

    public IntegrationManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = new ModuleLogger(null, "Integration");
        this.integrations = new HashMap<>();
    }

}
