package org.gi.gICore.manager.integrations;


import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import org.bukkit.OfflinePlayer;
import org.gi.gICore.GICore;

import java.util.Optional;

public class MMOCoreIntegration implements PluginIntegration{
    @Override
    public String getPluginName() {
        return "MMOCore";
    }

    @Override
    public boolean isEnabled() {
        return GICore.getInstance().getServer().getPluginManager().isPluginEnabled("MMOCore");
    }

    @Override
    public void disable() {

    }

    public Optional<PlayerData> getPlayerData(OfflinePlayer player) {
        try {
            PlayerData data = PlayerData.get(player);
            return Optional.ofNullable(data);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<PlayerClass> getPlayerClass(OfflinePlayer player) {
        return getPlayerData(player).map(PlayerData::getProfess);
    }

    public int getPlayerLevel(OfflinePlayer player) {
        return getPlayerData(player).map(PlayerData::getLevel).orElse(0);
    }

    public double getStat(OfflinePlayer player, String statName) {
        return getPlayerData(player)
                .map(data -> data.getStats().getStat(statName))
                .orElse(0.0);
    }
}
