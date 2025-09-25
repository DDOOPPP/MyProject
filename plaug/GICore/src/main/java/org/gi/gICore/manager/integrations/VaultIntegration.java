package org.gi.gICore.manager.integrations;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;

public class VaultIntegration implements PluginIntegration{
    private final Economy economy;

    public VaultIntegration(Economy economy) {
        this.economy = economy;
    }

    public String getPluginName(){
        return "Vault";
    }

    @Override
    public boolean isEnabled() {
        return economy != null;
    }

    @Override
    public void disable() {
        return;
    }

    public Economy getEconomy() {
        return economy;
    }

    public boolean hasAccount(OfflinePlayer player) {
        return economy.hasAccount(player);
    }

    public boolean has(OfflinePlayer player, double amount) {
        return economy.has(player, amount);
    }

    public double getBalance(OfflinePlayer player) {
        return economy.getBalance(player);
    }

    public boolean deposit(OfflinePlayer player, double amount) {
        return economy.depositPlayer(player,amount).transactionSuccess();
    }

    public boolean withdraw(OfflinePlayer player, double amount) {
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }
}
