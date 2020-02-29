package net.dashflight.data.postgres;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import net.dashflight.data.config.ConfigValue;
import net.dashflight.data.config.Configurable;
import net.dashflight.data.config.RuntimeEnvironment;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.guava.GuavaPlugin;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;


public class PostgresClient implements Configurable {

    private static final String APP_NAME = "java-postgres";


    @ConfigValue("pg_host")
    private String host;

    @ConfigValue("pg_port")
    private int port;

    @ConfigValue("pg_dbname")
    private String dbname;

    @ConfigValue("pg_username")
    private String username;

    @ConfigValue("pg_password")
    private String password;

    @ConfigValue("application_name")
    private String applicationName = "DataUtils application";

    @ConfigValue("max_pool_size")
    private int maxPoolSize = 2;


    private HikariConfig config = new HikariConfig();
    private HikariDataSource connectionPool;

    private Jdbi jdbi;


    PostgresClient(RuntimeEnvironment env, Map<String, Object> properties) {
        registerWith(RegistrationOptions.builder()
            .applicationName(APP_NAME)
            .environment(env)
            .additionalProperties(properties)
            .build()
        );
        init();
    }

    private void init() {
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


        jdbi = Jdbi.create(connectionPool);
        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.installPlugin(new PostgresPlugin());
        jdbi.installPlugin(new GuavaPlugin());
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

}
