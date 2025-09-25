package org.gi.gICore.manager;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.Result;
import org.gi.gICore.util.TransactionResult;
import org.gi.gICore.util.ValidationUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EconomyManager {
    private final JavaPlugin plugin;
    private final ModuleLogger logger;
    private final Economy economy;
    private final DataBaseManager dbManager;

    private final BigDecimal minAmount = BigDecimal.ZERO;
    private final BigDecimal maxAmount = new BigDecimal("999999999999");
    private final int decimalPlaces = 0;

    public EconomyManager(JavaPlugin plugin, DataBaseManager dbManager, Economy economy) {
        this.plugin = plugin;
        this.logger = new ModuleLogger(null, "Economy");
        this.economy = economy;
        this.dbManager = dbManager;

        if (economy == null) {
            logger.warn("Economy is null! Economic functions will not work.");
        }else {
            logger.info("Economy System initialized: "+ economy.getName());
        }
    }

    public Result<TransactionResult> withdraw(OfflinePlayer player, BigDecimal amount, String reason){
        if (!isAvailable()) {
            return Result.failure("Economy is not available");
        }
        ValidationUtil.requireNonNull(player, "player is Not Null");
        ValidationUtil.requireNonNull(amount, "amount is Not Null");

        if (!isValidAmount(amount)){
            return Result.failure("Invalid amount: "+amount);
        }

        return dbManager.executeTransaction(connection -> {
            try{

            }
        })
    }

    public BigDecimal getBalance(OfflinePlayer player) {
        if (!isAvailable()) {
            return BigDecimal.ZERO;
        }
        ValidationUtil.requireNonNull(player, "player is Not Null");

        try{
            double balance = economy.getBalance(player);
            return BigDecimal.valueOf(balance).setScale(decimalPlaces, RoundingMode.DOWN);
        }catch (Exception e){
            logger.error("Failed to get balance for: "+player.getName(), e);
            return BigDecimal.ZERO;
        }
    }
    public boolean isAvailable() { return economy != null; }
    public boolean has(OfflinePlayer player, BigDecimal amount) {
        return isAvailable() && economy.has(player, amount.doubleValue());
    }
    public String format(BigDecimal amount) {
        return isAvailable() ? economy.format(amount.doubleValue()) : amount.toString();
    }
    private boolean isValidAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(minAmount) > 0 && amount.compareTo(maxAmount) <= 0;
    }
}
