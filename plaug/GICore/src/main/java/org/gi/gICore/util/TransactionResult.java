package org.gi.gICore.util;

import lombok.Builder;
import lombok.Data;
import org.gi.gICore.model.Enum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResult {
    private final boolean success;
    private final Enum.TransactionType type;
    private final BigDecimal amount;
    private final BigDecimal previousBalance;
    private final BigDecimal newBalance;
    private final String reason;

    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();


    public BigDecimal getBalanceChange() {
        if (previousBalance == null || newBalance == null) return BigDecimal.ZERO;
        return newBalance.subtract(previousBalance);
    }

    @Override
    public String toString() {
        return String.format("%s: %s %s (Balance: %s -> %s) - %s",type,success ? "SUCCESS": "FAIL",amount,previousBalance,newBalance,reason);
    }
}
