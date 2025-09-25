package org.gi.gICore.manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.gi.gICore.configs.DataBaseConfig;
import org.gi.gICore.util.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class DataBaseManager {
    private DataBaseConfig config;
    private HikariDataSource dataSource;
    public DataBaseManager(DataBaseConfig config) {
        this.config = config;

        initialize();
    }

    private void initialize() {
        if (!config.isValid()){
            throw new IllegalArgumentException("Invalid database configuration");
        }

        HikariConfig hikariConfig = config.getHikariConfig();

        dataSource = new HikariDataSource(hikariConfig);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public <T> Result<T> executeTransaction(Function<Connection, Result<T>> operation) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            try {
                Result<T> result = operation.apply(connection);

                if (result.isSuccess()) {
                    connection.commit();
                } else {
                    connection.rollback();
                }

                return result;

            } catch (Exception e) {
                connection.rollback();
                return Result.failure("Transaction failed", e);
            }
        } catch (SQLException e) {
            return Result.failure("Failed to get connection", e);
        }
    }

    public <T> Result<T> executeTransaction(String sql,  PreparedStatementSetter setter, ResultSetMapper<T> mapper) {
        try(Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {

            if (setter != null) {
                setter.setValues(ps);
            }

            try(ResultSet resultSet = ps.executeQuery()) {
                T result = mapper.mapResultSet(resultSet);

                return Result.success(result);
            }
        } catch (SQLException e) {
            return Result.failure("Query execution failed: " + sql, e);
        }
    }

    /**
     * 업데이트 실행 (INSERT, UPDATE, DELETE)
     */
    public Result<Integer> executeUpdate(String sql, PreparedStatementSetter setter) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (setter != null) {
                setter.setValues(statement);
            }

            int rowsAffected = statement.executeUpdate();
            return Result.success(rowsAffected);

        } catch (SQLException e) {
            return Result.failure("Update execution failed: " + sql, e);
        }
    }

    /**
     * 배치 실행
     */
    public Result<int[]> executeBatch(String sql, BatchSetter batchSetter) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            batchSetter.setBatch(statement);
            int[] result = statement.executeBatch();

            return Result.success(result);

        } catch (SQLException e) {
            return Result.failure("Batch execution failed: " + sql, e);
        }
    }

    /**
     * 비동기 업데이트 실행
     */
    public CompletableFuture<Result<Integer>> executeUpdateAsync(String sql, PreparedStatementSetter setter) {
        return CompletableFuture.supplyAsync(() -> executeUpdate(sql, setter));
    }
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
    public boolean isHealthy() {
        try (Connection connection = getConnection()) {
            return connection.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }


    // 함수형 인터페이스들
    @FunctionalInterface
    public interface PreparedStatementSetter {
        void setValues(PreparedStatement statement) throws SQLException;
    }

    @FunctionalInterface
    public interface ResultSetMapper<T> {
        T mapResultSet(ResultSet resultSet) throws SQLException;
    }

    @FunctionalInterface
    public interface BatchSetter {
        void setBatch(PreparedStatement statement) throws SQLException;
    }
}
