package net.dashflight.data.postgres;

/**
 * Creates the connection options used by a Postgres client
 */
public interface PostgresConnectionOptionProvider {

    PostgresConnectionOptions get();

}
