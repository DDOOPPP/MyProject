package org.gi.gICore.model.log;

import lombok.Builder;
import lombok.Data;
import org.gi.gICore.model.Enum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TransactionLog {
    private UUID playerUUID;
    private String playerName;
    private Enum.TransactionType type;
    private BigDecimal amount;
    private BigDecimal previousBalance;
    private BigDecimal newBalance;
    private String reason;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
