package org.gi.gICore.manager.integrations;

public interface PluginIntegration {
    String getPluginName();
    boolean isEnabled();
    void disable();
}
