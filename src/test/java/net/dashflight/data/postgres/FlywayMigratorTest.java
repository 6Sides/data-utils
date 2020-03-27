package net.dashflight.data.postgres;

import static net.dashflight.data.postgres.FlywayMigrator.migrateAndExecute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class FlywayMigratorTest {

    @Rule
    public PostgresContainer postgresContainer = new PostgresContainer();


    @Before
    public void setup() throws Exception {
        migrateAndExecute(postgresContainer, postgres -> {
            try (Connection conn = postgres.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("insert into accounts.organizations (id, name) VALUES (?, ?), (?,?)");

                stmt.setInt(1, 1);
                stmt.setString(2, "Organization!");
                stmt.setInt(3, 2);
                stmt.setString(4, "Organization 2!");

                stmt.executeUpdate();
            }
        });
    }


    @Test
    public void test() throws Exception {
        migrateAndExecute(postgresContainer, postgres -> {
            try (Connection conn = postgres.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("select * from accounts.organizations");

                ResultSet res = stmt.executeQuery();

                while (res.next()) {
                    System.out.println(res.getString("name"));
                }
            }
        });
    }

    @Test
    public void test1() throws Exception {
        migrateAndExecute(postgresContainer, postgres -> {
            try (Connection conn = postgres.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("insert into accounts.organizations (id, name) VALUES (?, ?)");

                stmt.setInt(1, 10);
                stmt.setString(2, "Organization! !");

                stmt.executeUpdate();
            }
        });
    }

}