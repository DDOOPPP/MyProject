package org.gi.gICore.model.stat;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@Builder
public class DataBaseStat {
    private final int totalConnections;
    private final int activeConnections;
    private final int idleConnections;
    private final int threadsAwaitingConnection;

    public static DataBaseStat empty() {
        return DataBaseStat.builder()
                .totalConnections(0)
                .activeConnections(0)
                .idleConnections(0)
                .threadsAwaitingConnection(0)
                .build();
    }

    public double getUsagePercentage() {
        if (totalConnections == 0) return 0.0;
        return (double) activeConnections / totalConnections * 100.0;
    }

    @Override
    public String toString() {
        return String.format("DatabaseStats{total=%d, active=%d, idle=%d, waiting=%d, usage=%.1f%%}",
                totalConnections, activeConnections, idleConnections,
                threadsAwaitingConnection, getUsagePercentage());
    }
}
