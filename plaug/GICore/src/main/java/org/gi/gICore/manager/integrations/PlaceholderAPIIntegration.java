package org.gi.gICore.manager.integrations;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.gi.gICore.GICore;

public class PlaceholderAPIIntegration implements PluginIntegration {

    @Override
    public String getPluginName() {
        return "PlaceholderAPI";
    }

    @Override
    public boolean isEnabled() {
        return GICore.getInstance().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    @Override
    public void disable() {
        // PlaceholderAPI는 외부 플러그인이므로 비활성화하지 않음
    }

    public String setPlaceholders(Player player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}
