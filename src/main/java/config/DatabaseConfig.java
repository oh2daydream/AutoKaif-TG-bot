package config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class DatabaseConfig {
    private static HikariDataSource dataSource;

    public static DataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();

            // Читаем переменные окружения или берем дефолтные значения для туннеля
            String dbUrl = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5433/car_logbook");
            String dbUser = System.getenv().getOrDefault("DB_USER", "car_user");
            String dbPassword = System.getenv().getOrDefault("DB_PASSWORD", "Y6=7]Gfw#32F");

            config.setJdbcUrl(dbUrl);
            config.setUsername(dbUser);
            config.setPassword(dbPassword);

            // Базовые параметры пула соединений HikariCP
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(20000);

            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }
}