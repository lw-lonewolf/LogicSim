package com.logisim.data;

import com.logisim.domain.Circuit;
import com.logisim.domain.Connector;
import com.logisim.domain.components.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
}
