package net.dashflight.data.postgres;

import lombok.Builder;
import lombok.Data;

/**
 * Holds connection data for connecting to Postgres instance
 */
@Data
@Builder
public class PostgresConnectionOptions {

    /**
     * The host address of the instance
     */
    private String host;

    /**
     * The port of the instance
     */
    private int port;

    /**
     * The name of the database to connect to
     */
    private String dbname;

    /**
     * The username to authenticate with
     */
    private String username;

    /**
     * The password to authenticate with
     */
    private String password;

    /**
     * The name of the application connecting. This name shows up under `pg_stat_activity` table
     */
    @Builder.Default
    private String applicationName = "Java Application";

    /**
     * Maximum size of the connection pool
     */
    @Builder.Default
    private int maxPoolSize = 2;
}
