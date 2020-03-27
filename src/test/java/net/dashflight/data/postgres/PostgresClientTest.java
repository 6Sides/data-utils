package net.dashflight.data.postgres;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

public class PostgresClientTest {

    @Rule
    public GenericContainer postgresContainer = new GenericContainer<>("postgres:11.7-alpine")
            .withEnv("POSTGRES_USER", "postgres")
            .withEnv("POSTGRES_PASSWORD", "XpasswordX")
            .withEnv("POSTGRES_DB", "main")
            .withExposedPorts(5432);

    PostgresClient postgres;

    @Before
    public void setup() throws SQLException, IOException, NoSuchAlgorithmException {
        String address = postgresContainer.getContainerIpAddress();
        Integer port = postgresContainer.getFirstMappedPort();

        PostgresConnectionOptions options = PostgresConnectionOptions.builder()
                .host(address)
                .port(port)
                .username("postgres")
                .password("XpasswordX")
                .dbname("main")
                .build();

        postgres = new PostgresClient(() -> options);

        new FlywayManager(postgres).getFlyway().migrate();

        try (Connection conn = postgres.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("insert into accounts.organizations (id, name) VALUES (?, ?)");

            stmt.setInt(1, 1);
            stmt.setString(2, "Organization!");

            stmt.executeUpdate();
        }
    }

    @Test
    public void test() throws SQLException {
        try (Connection conn = postgres.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("select * from accounts.organizations where id = 1");

            ResultSet res = stmt.executeQuery();

            if (res.next()) {
                Assert.assertEquals(1, res.getInt("id"));
                Assert.assertEquals("Organization!", res.getString("name"));
            }
        }
    }

}