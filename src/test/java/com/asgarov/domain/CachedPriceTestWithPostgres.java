package com.asgarov.domain;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class CachedPriceTestWithPostgres {
    public static DockerImageName imageName =
            DockerImageName.parse("postgres:9.6.12");

    @Container
    public static PostgreSQLContainer postgres =
            new PostgreSQLContainer<>(imageName)
                    .withDatabaseName("theater_db")
                    .withUsername("theater")
                    .withPassword("password");

    private static Connection getConnection() throws SQLException {
        String url = String.format(
                "jdbc:postgresql://%s:%s/%s",
                postgres.getHost(),
                postgres.getFirstMappedPort(),
                postgres.getDatabaseName());

        return DriverManager.getConnection(url,
                postgres.getUsername(),
                postgres.getPassword());
    }

    @BeforeAll
    public static void setup() throws SQLException, IOException {
        var path = Path.of("src/test/resources/init.sql");
        var sql = Files.readString(path);
        try (Connection conn = getConnection()) {
            conn.createStatement().execute(sql);
        }
    }

    @Test
    public void emptyDatabase() throws SQLException {
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();
            ResultSet result = st.executeQuery("SELECT * FROM prices");
            assertEquals(0, result.getFetchSize());
        }
    }


}