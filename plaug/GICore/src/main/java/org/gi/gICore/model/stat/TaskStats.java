package org.gi.gICore.model.stat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskStats {
    private final int activeNamedTasks;
    private final int asyncExecutorActive;
    private final int asyncExecutorQueued;
    private final int scheduledExecutorActive;
    private final int scheduledExecutorQueued;

    @Override
    public String toString() {
        return String.format("TaskStats{named=%d, async=%d/%d, scheduled=%d/%d}",
                activeNamedTasks, asyncExecutorActive, asyncExecutorQueued,
                scheduledExecutorActive, scheduledExecutorQueued);
    }
}
