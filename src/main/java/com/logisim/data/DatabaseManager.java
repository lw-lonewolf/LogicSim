package com.logisim.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// FILE USES A SINGLETON DESIGN PATTERN

public class DatabaseManager {

    private static final String url =
        "jdbc:sqlite:" + System.getProperty("user.home") + "/logisim_data.db";
    private static DatabaseManager instance;

    private DatabaseManager() {
        createTables();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    private void createTables() {
        String sqlProjects = """
            CREATE TABLE IF NOT EXISTS projects(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            """;

        String sqlCircuits = """
            CREATE TABLE IF NOT EXISTS circuits(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                project_id INTEGER,
                name TEXT NOT NULL,
                FOREIGN KEY(project_id) REFERENCES projects(id) ON DELETE CASCADE
            );
            """;

        String sqlComponents = """
            CREATE TABLE IF NOT EXISTS components(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                circuit_id INTEGER,
                type TEXT NOT NULL,
                x_coord REAL,
                y_coord REAL,
                uuid TEXT,
                FOREIGN KEY(circuit_id) REFERENCES circuits(id) ON DELETE CASCADE
                );
            """;

        String sqlConnectors = """
            CREATE TABLE IF NOT EXISTS connectors(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                circuit_id INTEGER,
                source_comp_UUID TEXT,
                source_pin INTEGER,
                sink_comp_UUID TEXT,
                sink_pin INTEGER,
                FOREIGN KEY(circuit_id) REFERENCES circuits(id) ON DELETE CASCADE
            );
            """;

        try (
            Connection connection = getConnection();
            Statement stmt = connection.createStatement()
        ) {
            stmt.execute(sqlProjects);
            stmt.execute(sqlCircuits);
            stmt.execute(sqlComponents);
            stmt.execute(sqlConnectors);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
