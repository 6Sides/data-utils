package net.dashflight.data.postgres;

import net.dashflight.data.config.ConfigValue;
import net.dashflight.data.config.Configurable;

/**
 * Pulls the required postgres connection options from s3
 */
class DashflightPostgresConnectionOptionProvider implements PostgresConnectionOptionProvider, Configurable {

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
    private String applicationName = "Dashflight Java App";

    @ConfigValue("max_pool_size")
    private int maxPoolSize;


    public DashflightPostgresConnectionOptionProvider() {
        registerWith(APP_NAME);
    }


    @Override
    public PostgresConnectionOptions get() {
        return PostgresConnectionOptions.builder()
                .host(host)
                .port(port)
                .dbname(dbname)
                .username(username)
                .password(password)
                .applicationName(applicationName)
                .maxPoolSize(maxPoolSize)
                .build();
    }
}
