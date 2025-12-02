package com.logisim.data;

import com.logisim.domain.Circuit;
import com.logisim.domain.Connector;
import com.logisim.domain.components.And;
import com.logisim.domain.components.Bulb;
import com.logisim.domain.components.Component;
import com.logisim.domain.components.Not;
import com.logisim.domain.components.Or;
import com.logisim.domain.components.SubCircuitComponent;
import com.logisim.domain.components.Switch;
import com.logisim.ui.controllers.MainViewController;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) responsible for handling database operations
 * related to {@link Circuit} entities.
 * <p>
 * This class provides methods to create, retrieve, update, and delete circuits,
 * as well as managing the persistence of their associated components and connectors.
 * </p>
 */
public class CircuitDAO {

    /**
     * Saves a new circuit and its associated contents to the database.
     * <p>
     * This method first inserts the circuit record. If successful, it retrieves the
     * generated circuit ID and proceeds to save the circuit's components and connectors.
     * </p>
     *
     * @param circuit   The {@link Circuit} object containing the data to be saved.
     * @param projectId The unique identifier of the project to which this circuit belongs.
     */
    public void saveCircuit(Circuit circuit, long projectId) {
        String sql = "INSERT INTO circuits(project_id, name) VALUES (?, ?)";
        try (
            Connection conn = DatabaseManager.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
            )
        ) {
            stmt.setLong(1, projectId);
            stmt.setString(2, circuit.getName());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long circuitId = generatedKeys.getLong(1);
                    saveComponents(circuit, circuitId, conn);
                    saveConnectors(circuit, circuitId, conn);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Batch inserts the components of a circuit into the database.
     *
     * @param circuit   The circuit object containing the list of components.
     * @param circuitId The database ID of the circuit these components belong to.
     * @param conn      The active database connection to be used for the operation.
     * @throws SQLException If a database access error occurs or the SQL execution fails.
     */
    private void saveComponents(
        Circuit circuit,
        long circuitId,
        Connection conn
    ) throws SQLException {
        String sql =
            "INSERT INTO components(circuit_id, type, x_coord, y_coord, uuid) VALUES (?,?,?,?,?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Component comp : circuit.getComponents()) {
                pstmt.setLong(1, circuitId);
                pstmt.setString(2, comp.getName());
                pstmt.setDouble(3, comp.getPositionX());
                pstmt.setDouble(4, comp.getPositionY());
                pstmt.setString(5, comp.getUuid());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    /**
     * Batch inserts the connectors (wires) of a circuit into the database.
     *
     * @param circuit   The circuit object containing the list of connectors.
     * @param circuitId The database ID of the circuit these connectors belong to.
     * @param conn      The active database connection to be used for the operation.
     * @throws SQLException If a database access error occurs or the SQL execution fails.
     */
    private void saveConnectors(
        Circuit circuit,
        long circuitId,
        Connection conn
    ) throws SQLException {
        String sql =
            "INSERT INTO connectors(circuit_id, source_comp_uuid, source_pin, sink_comp_uuid, sink_pin) VALUES(?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Connector connObj : circuit.getConnectors()) {
                pstmt.setLong(1, circuitId);
                pstmt.setString(2, connObj.getSourceComp().getUuid());
                pstmt.setInt(3, connObj.getSource());
                pstmt.setString(4, connObj.getSinkComp().getUuid());
                pstmt.setInt(5, connObj.getSink());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    /**
     * Retrieves a list of circuits associated with a specific project.
     * <p>
     * Note: This method retrieves the circuit metadata (ID and name) but does not
     * automatically load the internal components or connectors.
     * </p>
     *
     * @param projectId The unique identifier of the project.
     * @return A {@link List} of {@link Circuit} objects containing IDs and names.
     */
    public List<Circuit> getCircuitsByProjectId(long projectId) {
        List<Circuit> circuits = new ArrayList<>();
        String sql = "SELECT id, name FROM circuits WHERE project_id = ?";

        try (
            Connection conn = DatabaseManager.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, projectId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Circuit c = new Circuit();
                c.setId(rs.getLong("id"));
                c.setName(rs.getString("name"));
                circuits.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return circuits;
    }

    /**
     * Creates a new empty circuit record in the database.
     *
     * @param projectId The unique identifier of the project.
     * @param name      The name to be assigned to the new circuit.
     */
    public void createCircuit(long projectId, String name) {
        String sql = "INSERT INTO circuits(project_id, name) VALUES(?, ?)";
        try (
            Connection conn = DatabaseManager.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, projectId);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Loads and reconstructs all components belonging to a specific circuit ID.
     * <p>
     * This method instantiates specific component classes (e.g., {@link And}, {@link Or},
     * {@link Switch}) based on the 'type' column stored in the database.
     * It also handles recursive loading for sub-circuits.
     * </p>
     *
     * @param circuitId The unique identifier of the circuit to load components from.
     * @return A {@link List} of fully constructed {@link Component} objects.
     */
    public List<Component> loadComponents(long circuitId) {
        System.out.println(
            "Attempting to load components for the id: " + circuitId
        );
        List<Component> components = new ArrayList<>();
        String sql =
            "SELECT type, x_coord, y_coord, uuid FROM components WHERE circuit_id = ?";

        try (
            Connection conn = DatabaseManager.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, circuitId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String type = rs.getString("type");
                double x = rs.getDouble("x_coord");
                double y = rs.getDouble("y_coord");
                String uuid = rs.getString("uuid");
                Component comp = switch (type) {
                    case "and" -> new And();
                    case "or" -> new Or();
                    case "not" -> new Not();
                    case "switch" -> new Switch();
                    case "bulb" -> new Bulb();
                    case "subcircuitcomponent" -> {
                        long refid = rs.getLong("ref_circuit_id");
                        Circuit inner =
                            new MainViewController().loadFullCircuitFromDB(
                                refid
                            );
                        yield new SubCircuitComponent(inner);
                    }
                    default -> null;
                };

                if (comp != null) {
                    comp.setPositionX(x);
                    comp.setPositionY(y);
                    comp.setUuid(uuid);
                    components.add(comp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return components;
    }

    /**
     * A record representing the raw data of a connection between two components.
     *
     * @param sourceUuid The UUID of the source component.
     * @param sourcePin  The pin index on the source component.
     * @param sinkUuid   The UUID of the destination (sink) component.
     * @param sinkPin    The pin index on the destination component.
     */
    public record ConnectionRecord(
        String sourceUuid,
        int sourcePin,
        String sinkUuid,
        int sinkPin
    ) {}

    /**
     * Retrieves the raw connection data for a specific circuit.
     *
     * @param circuitId The unique identifier of the circuit.
     * @return A {@link List} of {@link ConnectionRecord} objects representing the connections.
     */
    public List<ConnectionRecord> loadConnections(long circuitId) {
        List<ConnectionRecord> connections = new ArrayList<>();
        String sql =
            "SELECT source_comp_uuid, source_pin, sink_comp_uuid, sink_pin FROM connectors WHERE circuit_id = ?";

        try (
            Connection conn = DatabaseManager.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, circuitId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                connections.add(
                    new ConnectionRecord(
                        rs.getString("source_comp_uuid"),
                        rs.getInt("source_pin"),
                        rs.getString("sink_comp_uuid"),
                        rs.getInt("sink_pin")
                    )
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connections;
    }

    /**
     * Updates an existing circuit in the database using a transactional approach.
     * <p>
     * This method performs the following steps:
     * 1. Updates the circuit name.
     * 2. Deletes all existing components associated with the circuit.
     * 3. Deletes all existing connectors associated with the circuit.
     * 4. Inserts the current state of components.
     * 5. Inserts the current state of connectors.
     * </p>
     *
     * @param circuit The {@link Circuit} object containing the updated data and ID.
     */
    public void updateCircuit(Circuit circuit) {
        String sqlUpdateName = "UPDATE circuits SET name = ? WHERE id = ?";
        String sqlDeleteComps = "DELETE FROM components WHERE circuit_id = ?";
        String sqlDeleteConns = "DELETE FROM connectors WHERE circuit_id = ?";

        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            try (
                PreparedStatement pstmt = conn.prepareStatement(sqlUpdateName)
            ) {
                pstmt.setString(1, circuit.getName());
                pstmt.setLong(2, circuit.getId());
                pstmt.executeUpdate();
            }

            try (
                PreparedStatement p1 = conn.prepareStatement(sqlDeleteComps);
                PreparedStatement p2 = conn.prepareStatement(sqlDeleteConns)
            ) {
                p1.setLong(1, circuit.getId());
                p1.executeUpdate();

                p2.setLong(1, circuit.getId());
                p2.executeUpdate();
            }

            saveComponents(circuit, circuit.getId(), conn);

            saveConnectors(circuit, circuit.getId(), conn);

            conn.commit();
            System.out.println(
                "Circuit Updated Successfully: " + circuit.getName()
            );
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Deletes a circuit from the database.
     * <p>
     * Due to database foreign key constraints (cascading deletes), removing the circuit
     * usually removes associated components and connectors automatically.
     * </p>
     *
     * @param id The unique identifier of the circuit to be deleted.
     */
    public void deleteCircuit(long id) {
        String sql = "DELETE FROM circuits WHERE id = ?";
        try (
            java.sql.Connection conn =
                DatabaseManager.getInstance().getConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
            System.out.println("Circuit deleted: " + id);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }
}
