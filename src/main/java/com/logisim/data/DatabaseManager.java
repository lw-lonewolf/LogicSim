package com.logisim.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// FILE USES A SINGLETON DESIGN PATTERN

/**
 * Manages the SQLite database connection and schema initialization for the application.
 * <p>
 * This class implements the Singleton design pattern to ensure a centralized point
 * of access for database operations. It handles the creation of the database file
 * (stored in the user's home directory) and initializes the necessary tables
 * if they do not already exist.
 * </p>
 */
public class DatabaseManager {

    /**
     * The JDBC connection URL string pointing to the SQLite database file
     * located in the user's home directory.
     */
    private static final String url =
        "jdbc:sqlite:" + System.getProperty("user.home") + "/logisim_data.db";

    /**
     * The single instance of the DatabaseManager class.
     */
    private static DatabaseManager instance;

    /**
     * Private constructor to enforce the Singleton design pattern.
     * <p>
     * When instantiated, it automatically attempts to create the necessary
     * database tables.
     * </p>
     */
    private DatabaseManager() {
        createTables();
    }

    /**
     * Retrieves the singleton instance of the DatabaseManager.
     * <p>
     * If the instance does not exist, it is created.
     * </p>
     *
     * @return The single instance of {@link DatabaseManager}.
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Establishes and returns a new connection to the SQLite database.
     *
     * @return A {@link Connection} object connected to the database.
     * @throws SQLException If a database access error occurs.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    /**
     * Initializes the database schema by creating required tables if they do not exist.
     * <p>
     * The following tables are created:
     * <ul>
     *   <li><b>projects</b>: Stores project metadata.</li>
     *   <li><b>circuits</b>: Stores circuits linked to projects.</li>
     *   <li><b>components</b>: Stores individual components within circuits.</li>
     *   <li><b>connectors</b>: Stores wiring connections between components.</li>
     * </ul>
     * </p>
     */
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
