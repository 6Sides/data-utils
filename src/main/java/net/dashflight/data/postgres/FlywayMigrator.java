package net.dashflight.data.postgres;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Migrates the test container to the correct version and returns a client connected to it.
 */
public class FlywayMigrator {

    public static void migrateAndExecute(PostgresContainer container, PostgresTestCase testCase) throws Exception {
        if (container == null) {
            throw new IllegalArgumentException("Container cannot be null");
        }

        String address = container.getContainerIpAddress();
        int port = container.getFirstMappedPort();

        Map<String, String> env = container.getEnvMap();

        String username = env.get("POSTGRES_USER");
        String password = env.get("POSTGRES_PASSWORD");
        String database = env.get("POSTGRES_DB");


        PostgresConnectionOptions options = PostgresConnectionOptions.builder()
                .host(address)
                .port(port)
                .username(username)
                .password(password)
                .dbname(database)
                .build();


        try (PostgresClient pgClient = new PostgresClient(() -> options)) {
            try {
                new FlywayManager(pgClient).getFlyway().migrate();
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
                throw new RuntimeException("Flyway could not migrate database");
            }


            testCase.run(pgClient);
        }
    }


    interface PostgresTestCase {
        void run(PostgresClient client) throws Exception;
    }
}
