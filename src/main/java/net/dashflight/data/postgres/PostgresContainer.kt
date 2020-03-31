package net.dashflight.data.postgres;

import org.testcontainers.containers.GenericContainer;

/**
 * Creates a postgres version with the required environment variables set
 */
public class PostgresContainer extends GenericContainer<PostgresContainer> {

    public PostgresContainer() {
        super("postgres:11.7-alpine");

        withEnv("POSTGRES_USER", "postgres");
        withEnv("POSTGRES_PASSWORD", "XpasswordX");
        withEnv("POSTGRES_DB", "main");
        withExposedPorts(5432);
    }
}
