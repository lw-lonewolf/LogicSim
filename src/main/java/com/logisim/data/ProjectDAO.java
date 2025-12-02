package com.logisim.data;

import com.logisim.domain.Circuit;
import com.logisim.domain.Project;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {

    private final CircuitDAO circuitDAO = new CircuitDAO();

    public void saveProject(Project project) {
        String sql = "INSERT INTO projects(name) VALUES(?)";

        try (
            Connection conn = DatabaseManager.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
            );
        ) {
            pstmt.setString(1, project.getName());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long projectId = generatedKeys.getLong(1);

                    for (Circuit c : project.getCircuits()) {
                        circuitDAO.saveCircuit(c, projectId);
                    }
                }
            }
            System.out.println("Project Saved.");
        } catch (SQLException e) {
            System.out.println("Error Saving:" + e.getMessage());
        }
    }

    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT id, name FROM projects ORDER BY created_at DESC";

        try (
            Connection conn = DatabaseManager.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                projects.add(new Project(id, name));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return projects;
    }

    public void deleteProject(long id) {
        String sql = "DELETE FROM projects WHERE id = ?";
        try (
            java.sql.Connection conn =
                DatabaseManager.getInstance().getConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
            System.out.println("Project deleted: " + id);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }
}
