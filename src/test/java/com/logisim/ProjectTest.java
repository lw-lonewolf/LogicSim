package com.logisim;

import static org.junit.jupiter.api.Assertions.*;

import com.logisim.data.ProjectDAO;
import com.logisim.domain.Circuit;
import com.logisim.domain.Project;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ProjectTest {

    @Test
    void testDefaultConstructor() {
        Project project = new Project();
        assertEquals("Project", project.getName());
        assertNotNull(project.getCircuits());
        assertTrue(project.getCircuits().isEmpty());
    }

    @Test
    void testNameConstructor() {
        Project project = new Project("My CPU");
        assertEquals("My CPU", project.getName());
    }

    @Test
    void testFullConstructor() {
        Project project = new Project(5, "ALU Design");
        assertEquals(5, project.getId());
        assertEquals("ALU Design", project.getName());
    }

    @Test
    void testSettersAndGetters() {
        Project project = new Project();
        project.setId(10);
        project.setName("Updated Name");

        assertEquals(10, project.getId());
        assertEquals("Updated Name", project.getName());
    }

    @Test
    void testCircuitManagement() {
        Project project = new Project();
        List<Circuit> circuits = new ArrayList<>();

        Circuit c1 = new Circuit();
        c1.setName("Sub 1");
        circuits.add(c1);

        project.setCircuits(circuits);

        assertEquals(1, project.getCircuits().size());
        assertEquals("Sub 1", project.getCircuits().get(0).getName());
    }

    @Test
    void testToString() {
        Project project = new Project("Display Name");
        assertEquals("Display Name", project.toString());
    }

    @Test
    void testSaveMock() {
        Project project = new Project("Test Save");

        assertDoesNotThrow(project::save);
    }

    @Test
    void testLoadAndExportPlaceholders() {
        Project project = new Project();
        assertDoesNotThrow(project::load);
        assertDoesNotThrow(project::export);
    }

    @Test
    void testDAOInjection() {
        Project project = new Project();
        ProjectDAO dao = new ProjectDAO();
        project.setProjectdao(dao);
        assertSame(dao, project.getProjectdao());
    }
}
