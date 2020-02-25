package net.dashflight.data.postgres;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import config.parser.ConfigValue;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.guava.GuavaPlugin;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;


public class PostgresConnectionPool {

    @ConfigValue("pg_host")
    private static String host;

    @ConfigValue("pg_port")
    private static int port;

    @ConfigValue("pg_dbname")
    private static String dbname;

    @ConfigValue("pg_username")
    private static String username;

    @ConfigValue("pg_password")
    private static String password;

    @ConfigValue("application_name")
    private static String applicationName = "DataUtils application";

    @ConfigValue("max_pool_size")
    private static int maxPoolSize = 2;


    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource connectionPool;

    private static Jdbi jdbi;


    private PostgresConnectionPool() {}

    public static void setApplicationName(String name) {
        applicationName = name;
    }

    private static void init() {
        String dbUrl = String.format("jdbc:postgresql://%s:%s/?dbname=%s&user=%s&password=%s",
                host,
                port,
                dbname,
                username,
                password,
                applicationName);

        URI dbUri = null;
        try {
            dbUri = new URI(dbUrl);
        } catch (URISyntaxException e) {
            // Kill the program
            System.exit(1);
        }

        if (dbUri.getUserInfo() != null) {
            config.setUsername(dbUri.getUserInfo().split(":")[0]);
            config.setPassword(dbUri.getUserInfo().split(":")[1]);
        }

        config.setJdbcUrl(dbUrl);
        config.setPoolName(applicationName);
        config.setMaximumPoolSize(maxPoolSize);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("ApplicationName", applicationName);
        connectionPool = new HikariDataSource(config);


        Runtime.getRuntime().addShutdownHook(new Thread(() -> connectionPool.close()));

        jdbi = Jdbi.create(PostgresConnectionPool.getDataSource());
        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.installPlugin(new PostgresPlugin());
        jdbi.installPlugin(new GuavaPlugin());
    }

    public static Connection getConnection() throws SQLException {
        if (connectionPool == null) {
            init();
        }

        return connectionPool.getConnection();
    }

    public static DataSource getDataSource() {
        return connectionPool;
    }

    public static Jdbi getJdbi() {
        return jdbi;
    }

}
