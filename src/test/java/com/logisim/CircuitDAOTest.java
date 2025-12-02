package com.logisim;

import static org.junit.jupiter.api.Assertions.*;

import com.logisim.data.CircuitDAO;
import com.logisim.domain.Circuit;
import com.logisim.domain.components.And;
import java.util.List;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CircuitDAOTest {

    private static final CircuitDAO circuitDAO = new CircuitDAO();
    private static final long TEST_PROJECT_ID = 1;

    @Test
    @Order(1)
    void createCircuit() {
        assertDoesNotThrow(() -> {
            circuitDAO.createCircuit(TEST_PROJECT_ID, "Test Circuit Unit");
        });
    }

    @Test
    @Order(2)
    void saveCircuitWithComponents() {
        Circuit c = new Circuit();
        c.setName("Complex Save Test");

        And andGate = new And();
        andGate.setPositionX(50);
        andGate.setPositionY(50);

        c.addComponent(andGate);

        assertDoesNotThrow(() -> {
            circuitDAO.saveCircuit(c, TEST_PROJECT_ID);
        });
    }

    @Test
    @Order(3)
    void getCircuitsByProjectId() {
        List<Circuit> circuits = circuitDAO.getCircuitsByProjectId(
            TEST_PROJECT_ID
        );
        assertNotNull(circuits);
        assertTrue(circuits.size() > 0);
    }
}
