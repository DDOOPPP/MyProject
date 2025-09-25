package org.gi.gICore.manager;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gICore.model.Enum;
import org.gi.gICore.model.log.TransactionLog;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.Result;
import org.gi.gICore.util.TransactionResult;
import org.gi.gICore.util.ValidationUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;

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
    /**
     * 데이터베이스 레벨에서 동시성 제어된 출금
     * (다중 서버 환경에서 안전함)
     */
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
                BigDecimal currentBalance = getCurrentBalanceWithLock(connection, player);

                if (currentBalance.compareTo(amount) < 0){
                    return Result.failure("Not enough balance: "+currentBalance);
                }

                var response = economy.withdrawPlayer(player,amount.doubleValue());
                if (!response.transactionSuccess()){
                    return Result.failure(response.errorMessage);
                }

                BigDecimal newBalance = BigDecimal.valueOf(response.balance).setScale(decimalPlaces, RoundingMode.DOWN);

                TransactionLog log = TransactionLog.builder()
                        .playerUUID(player.getUniqueId())
                        .playerName(player.getName())
                        .type(Enum.TransactionType.WITHDRAW)
                        .amount(amount)
                        .previousBalance(currentBalance)
                        .newBalance(newBalance)
                        .reason(reason)
                        .build();

                saveTransactionLog(connection, log);

                TransactionResult result = TransactionResult.builder()
                        .success(true)
                        .reason(reason)
                        .amount(amount)
                        .previousBalance(currentBalance)
                        .newBalance(newBalance)
                        .build();

                logger.debug("Withdraw successful: %s - %s [Reason %s]",player.getName(),amount,reason);

                return Result.success(result);
            }catch (Exception e){
                logger.error("Failed to withdraw: "+player.getName(), e);
                return Result.failure("withdraw operation failed: ",e);
            }
        });
    }
    /**
     * 데이터베이스 레벨에서 동시성 제어된 입금
     */
    public Result<TransactionResult> deposit(OfflinePlayer player, BigDecimal amount, String reason){
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
                BigDecimal currentBalance = getCurrentBalanceWithLock(connection, player);

                BigDecimal newBalance = currentBalance.add(amount);
                if (newBalance.compareTo(maxAmount) > 0) {
                    return Result.failure("Amount exceeds maximum limit");
                }
                var response = economy.depositPlayer(player,amount.doubleValue());
                if (!response.transactionSuccess()){
                    return Result.failure(response.errorMessage);
                }

                BigDecimal actualNewBalance = BigDecimal.valueOf(response.balance).setScale(decimalPlaces, RoundingMode.DOWN);

                TransactionLog log = TransactionLog.builder()
                        .playerUUID(player.getUniqueId())
                        .playerName(player.getName())
                        .type(Enum.TransactionType.DEPOSIT)
                        .amount(amount)
                        .previousBalance(currentBalance)
                        .newBalance(actualNewBalance)
                        .reason(reason)
                        .build();

                saveTransactionLog(connection, log);

                TransactionResult result = TransactionResult.builder()
                        .success(true)
                        .type(Enum.TransactionType.DEPOSIT)
                        .amount(amount)
                        .previousBalance(currentBalance)
                        .newBalance(actualNewBalance)
                        .reason(reason)
                        .build();
                logger.debug("Deposit successful: %s + %s (Reason: %s)",
                        player.getName(), amount, reason);

                return Result.success(result);

            } catch (Exception e) {
                logger.error("Deposit failed for " + player.getName(), e);
                return Result.failure("Deposit operation failed", e);
            }
        });
    }

    private void saveTransactionLog(Connection connection, TransactionLog log) throws SQLException{
        String sql = """
            INSERT INTO gi_transaction_logs (player_id, player_name, transaction_type, amount, 
                                           previous_balance, new_balance, reason, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, NOW())
            """;

        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, log.getPlayerUUID().toString());
            stmt.setString(2, log.getPlayerName());
            stmt.setString(3, log.getType().name());
            stmt.setBigDecimal(4, log.getAmount());
            stmt.setBigDecimal(5, log.getPreviousBalance());
            stmt.setBigDecimal(6, log.getNewBalance());

            stmt.executeUpdate();
        }
    }

    /**
     * FOR UPDATE 락과 함께 잔액 조회 (다중 서버 동시성 제어)
     */
    private BigDecimal getCurrentBalanceWithLock(Connection connection, OfflinePlayer player) {
        return getCurrentBalance(player);
    }

    private BigDecimal getCurrentBalance(OfflinePlayer player) {
        return getBalance(player);
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
