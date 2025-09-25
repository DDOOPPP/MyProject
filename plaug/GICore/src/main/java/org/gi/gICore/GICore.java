package org.gi.gICore;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class GICore extends JavaPlugin {
    private static GICore instance;
    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static GICore getInstance() {
        return instance;
    }

    public static Logger getLog() {
        return instance.getLogger();
    }
}
