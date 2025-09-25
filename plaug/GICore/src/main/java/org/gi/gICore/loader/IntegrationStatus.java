package org.gi.gICore.loader;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IntegrationStatus {
    private final boolean vault;
    private final boolean mmoItems;
    private final boolean mmoCore;
    private final boolean placeholderAPI;
    private final int totalIntegrations;

    public boolean isAllEnabled() {
        return vault && mmoItems && mmoCore && placeholderAPI;
    }

    @Override
    public String toString(){
        return String.format("IntegrationStatus{vault=%s, mmoItems=%s, mmoCore=%s, placeholderAPI=%s, total=%d}",
                vault, mmoItems, mmoCore, placeholderAPI, totalIntegrations);
    }
}
