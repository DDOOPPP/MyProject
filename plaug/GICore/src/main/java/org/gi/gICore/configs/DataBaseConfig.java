package org.gi.gICore.configs;

import com.zaxxer.hikari.HikariConfig;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

@Data
@Getter
public class DataBaseConfig {
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    private final int maxPoolSize;
    private final int minPoolSize;
    private final long connectionTimeout;
    private final long idleTimeout;
    private final long maxLifetime;

    public DataBaseConfig(ConfigurationSection section) {
        this.host = section.getString("host");
        this.port = section.getInt("port");
        this.database = section.getString("database");
        this.username = section.getString("username");
        this.password = section.getString("password");

        this.maxPoolSize = section.getInt("maxPoolSize");
        this.minPoolSize = section.getInt("minPoolSize");
        this.connectionTimeout = section.getLong("connectionTimeout");
        this.idleTimeout = section.getLong("idleTimeout");
        this.maxLifetime = section.getLong("maxLifetime");
    }

    public String getURL(){
        return String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul",
                host, port, database);
    }

    public boolean isValid() {
        return host != null && !host.isEmpty()
                && port > 0 && port <= 65535
                && database != null && !database.isEmpty()
                && username != null
                && maxPoolSize > 0
                && minPoolSize >= 0
                && connectionTimeout > 0
                && idleTimeout > 0
                && maxLifetime > 0;
    }

    public HikariConfig getHikariConfig() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(getURL());
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minPoolSize);
        config.setConnectionTimeout(connectionTimeout);
        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(maxLifetime);

        return config;
    }
}
