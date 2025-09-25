package org.gi.gICore.model.stat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EconomyStat {
    private final String economyName;
    private final boolean available;
    private final int activeLocks;
    private final String currencyName;

    @Override
    public String toString() {
        return String.format("EconomyStat{name=%s, available=%s, locks=%d, currency=%s}",
                economyName, available, activeLocks, currencyName);
    }

}
