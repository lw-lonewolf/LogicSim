package com.logisim;

import static org.junit.jupiter.api.Assertions.*;

import com.logisim.business.And;
import com.logisim.business.Circuit;
import com.logisim.business.Component;
import com.logisim.business.Connector;
import com.logisim.business.Or;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CircuitTests {

    @Test
    void newCircuitStartsEmpty() {
        Circuit c = new Circuit();
        boolean noconnectors = c.getConnectors().isEmpty();
        boolean nocomponents = c.getComponents().isEmpty();
        assertTrue(noconnectors && nocomponents);
    }

    @Test
    void addComponentWorks() {
        Circuit c = new Circuit();
        Component comp = new And();
        c.addComponent(comp);
        assertEquals(1, c.getComponents().size());
    }

    @Test
    void addComponentThrows() {
        Circuit c = new Circuit();
        assertThrows(IllegalArgumentException.class, () ->
            c.addComponent(null)
        );
    }

    @Test
    void removeComponentWorks() {
        Circuit c = new Circuit();
        Component comp = new And();
        c.addComponent(comp);
        c.removeComponent(0);
    }

    @Test
    void addComponentThrowsOnSameAddition() {
        Circuit c = new Circuit();
        Component comp = new And();
        c.addComponent(comp);
        assertThrows(IllegalArgumentException.class, () ->
            c.addComponent(comp)
        );
    }

    @Test
    void removeComponentThrowsOnNegativeIndex() {
        Circuit c = new Circuit();
        assertThrows(InvalidParameterException.class, () ->
            c.removeComponent(-1)
        );
    }

    @Test
    void removeComponentThrowsOnGreaterThanSizeIndex() {
        Circuit c = new Circuit();
        Component comp = new And();
        c.addComponent(comp);
        assertThrows(InvalidParameterException.class, () ->
            c.removeComponent(1)
        );
    }

    @Test
    void removeComponentAlsoRemovesConnectors() {
        Circuit c = new Circuit();
        Component comp1 = new And();
        Component comp2 = new And();
        Component comp3 = new Or();
        c.addComponent(comp1);
        c.addComponent(comp2);
        c.addComponent(comp3);
        c.addConnection(0, 0, 0, 1);
        c.addConnection(0, 1, 0, 2);
        c.removeComponent(1);
        assertFalse(c.getComponents().contains(comp2));

        for (Connector conn : c.getConnectors()) {
            assertNotEquals(comp2, conn.getSourceComp());
            assertNotEquals(comp2, conn.getSinkComp());
        }

        assertEquals(0, c.getConnectors().size());
    }

    @Test
    void addConnectionThrowsWhenSinkOrSourceDoesntExist() {
        Circuit c = new Circuit();
        Component comp1 = new And();
        Component comp2 = new And();
        c.addComponent(comp1);
        c.addComponent(comp2);
        assertThrows(InvalidParameterException.class, () ->
            c.addConnection(0, 0, -1, 0)
        );
    }

    @Test
    void addConnectionThrowsWhenSinkOrSourceIndexDoesntExist() {
        Circuit c = new Circuit();
        Component comp1 = new And();
        Component comp2 = new And();
        c.addComponent(comp1);
        c.addComponent(comp2);
        assertThrows(InvalidParameterException.class, () ->
            c.addConnection(0, -1, 0, 0)
        );
    }

    @Test
    void connectionSendsSignalSuccessfully() {
        Circuit c = new Circuit();
        Component comp1 = new And();
        Component comp2 = new Or();
        c.addComponent(comp1);
        c.addComponent(comp2);
        comp1.setInput(0, true);
        comp1.setInput(1, true);
        comp1.execute();
        c.addConnection(0, 0, 0, 1);
        Connector current = c.getConnectors().get(0);
        current.process();
        comp2.setInput(1, false);
        comp2.execute();
        assertTrue(comp2.getOutput(0));
    }

    @Test
    void simulateSingleConnection() {
        Circuit circuit = new Circuit();
        Component comp1 = new And();
        Component comp2 = new Or();
        circuit.addComponent(comp1);
        circuit.addComponent(comp2);
        comp1.setInput(0, true);
        comp1.setInput(1, true);
        comp2.setInput(1, false);
        circuit.addConnection(0, 0, 0, 1);
        circuit.simulate();
        assertTrue(comp2.getOutput(0));
    }

    @Test
    void simulateMultipleComponents() {
        Circuit circuit = new Circuit();
        Component comp1 = new And();
        Component comp2 = new Or();
        Component comp3 = new And();
        circuit.addComponent(comp1);
        circuit.addComponent(comp2);
        circuit.addComponent(comp3);
        comp1.setInput(0, true);
        comp1.setInput(1, true);
        comp2.setInput(1, false);
        comp3.setInput(1, true);
        circuit.addConnection(0, 0, 0, 1);
        circuit.addConnection(0, 1, 0, 2);
        circuit.simulate();
        assertTrue(comp3.getOutput(0));
    }

    @Test
    void truthTablehasCorrectLength() {
        Circuit circuit = new Circuit();
        Component comp1 = new And();
        Component comp2 = new Or();
        Component comp3 = new And();
        circuit.addComponent(comp1);
        circuit.addComponent(comp2);
        circuit.addComponent(comp3);
        comp1.setInput(0, true);
        comp1.setInput(1, true);
        comp2.setInput(1, false);
        comp3.setInput(1, true);
        circuit.addConnection(0, 0, 0, 1);
        circuit.addConnection(0, 1, 0, 2);
        boolean[][] truthTable = circuit.analyze();
        int nRows = truthTable.length;
        int nColumns = truthTable[0].length;
        assertEquals(16, nRows);
        assertEquals(5, nColumns);
    }

    @Test
    void truthTableProducesCorrectValues() {
        Circuit circuit = new Circuit();
        Component comp1 = new And();
        Component comp2 = new Or();
        Component comp3 = new And();
        circuit.addComponent(comp1);
        circuit.addComponent(comp2);
        circuit.addComponent(comp3);
        comp1.setInput(0, true);
        comp1.setInput(1, true);
        comp2.setInput(1, false);
        comp3.setInput(1, true);
        circuit.addConnection(0, 0, 0, 1);
        circuit.addConnection(0, 1, 0, 2);
        boolean[][] truthTable = circuit.analyze();

        assertFalse(truthTable[0][0]);
        assertFalse(truthTable[0][1]);
        // Row 14: 1 1 0 1 1
        assertTrue(
            truthTable[13][0] &&
                truthTable[13][1] &&
                !truthTable[13][2] &&
                truthTable[13][3] &&
                truthTable[13][4]
        );
    }

    @Test
    void testGenerateBooleanExpressions() {
        boolean[][] truthTable = {
            { false, false, false },
            { false, true, false },
            { true, false, false },
            { true, true, true },
        };

        List<String> inputNames = Arrays.asList("A", "B");
        Circuit circuit = new Circuit();
        String expr = circuit.generateBooleanExpression(truthTable, inputNames);
        assertEquals("(A & B)", expr);
    }

    @Test
    void testGenerateBooleanExpressionMultipleTerms() {
        boolean[][] truthTable = {
            { false, false, false },
            { false, true, true },
            { true, false, true },
            { true, true, true },
        };
        List<String> inputNames = Arrays.asList("A", "B");

        Circuit circuit = new Circuit();
        String expr = circuit.generateBooleanExpression(truthTable, inputNames);
        assertEquals("(!A & B) + (A & !B) + (A & B)", expr);
    }

    @Test
    void testCircuitAnalyzeAndBooleanExpression() {
        Circuit circuit = new Circuit();

        Component comp1 = new And();
        Component comp2 = new Or();
        Component comp3 = new And();

        circuit.addComponent(comp1);
        circuit.addComponent(comp2);
        circuit.addComponent(comp3);

        comp1.setInput(0, true);
        comp1.setInput(1, true);
        comp2.setInput(1, false);
        comp3.setInput(1, true);

        circuit.addConnection(0, 0, 0, 1); // comp1.output[0] -> comp2.input[0]
        circuit.addConnection(0, 1, 0, 2); // comp1.output[1] -> comp3.input[0]

        boolean[][] truthTable = circuit.analyze();

        int nInputs = 4;
        assertEquals(16, truthTable.length);
        int nOutputs = 1;
        assertEquals(nInputs + nOutputs, truthTable[0].length);

        for (int i = 0; i < nInputs; i++) {
            assertFalse(truthTable[0][i]);
        }
        assertFalse(truthTable[0][nInputs]);

        List<String> inputNames = Arrays.asList("I0", "I1", "I2", "I3");
        String expr = circuit.generateBooleanExpression(truthTable, inputNames);

        assertTrue(expr.contains("&") || expr.contains("+"));
        assertFalse(expr.isEmpty());
    }
}
