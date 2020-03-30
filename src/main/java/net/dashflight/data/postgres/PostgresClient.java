package net.dashflight.data.postgres;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import net.dashflight.data.config.Configurable;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.guava.GuavaPlugin;
import org.jdbi.v3.jodatime2.JodaTimePlugin;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

/**
 * Creates a pool of Postgres connections
 */
@Singleton
public class PostgresClient implements AutoCloseable {

    private HikariConfig config = new HikariConfig();
    private HikariDataSource connectionPool;

    private Jdbi jdbi;

    private final PostgresConnectionOptions options;

    @Inject
    PostgresClient(PostgresConnectionOptionProvider optionProvider) {
        this.options = optionProvider.get();
        init();
    }

    private void init() {
        String dbUrl = String.format("jdbc:postgresql://%s:%s/%s?user=%s&password=%s",
                options.getHost(),
                options.getPort(),
                options.getDbname(),
                options.getUsername(),
                options.getPassword()
        );

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
        config.setPoolName(options.getApplicationName());
        config.setMaximumPoolSize(options.getMaxPoolSize());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("ApplicationName", options.getApplicationName());
        connectionPool = new HikariDataSource(config);

        // Close the pool on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> connectionPool.close()));


        jdbi = Jdbi.create(connectionPool);
        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.installPlugin(new PostgresPlugin());
        jdbi.installPlugin(new GuavaPlugin());
        jdbi.installPlugin(new JodaTimePlugin());
    }

    public Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }

    public DataSource getDataSource() {
        return connectionPool;
    }

    public Jdbi getJdbi() {
        return jdbi;
    }


    @Override
    public void close() {
        connectionPool.close();
    }
}
