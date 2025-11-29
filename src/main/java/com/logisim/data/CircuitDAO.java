package com.logisim.data;

import com.logisim.domain.Circuit;
import com.logisim.domain.Connector;
import com.logisim.domain.components.And;
import com.logisim.domain.components.Bulb;
import com.logisim.domain.components.Component;
import com.logisim.domain.components.Not;
import com.logisim.domain.components.Or;
import com.logisim.domain.components.Switch;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CircuitDAO {

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

    public record ConnectionRecord(
        String sourceUuid,
        int sourcePin,
        String sinkUuid,
        int sinkPin
    ) {}

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
}
