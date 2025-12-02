package com.logisim;

import static org.junit.jupiter.api.Assertions.*;

import com.logisim.domain.Circuit;
import com.logisim.domain.components.And;
import com.logisim.domain.components.Bulb;
import com.logisim.domain.components.Not;
import com.logisim.domain.components.Switch;
import org.junit.jupiter.api.Test;

class CircuitTest {

    @Test
    void simpleCircuitSimulation() {
        Circuit circuit = new Circuit();

        Switch sw = new Switch();
        Not notGate = new Not();
        Bulb bulb = new Bulb();

        circuit.addComponent(sw);
        circuit.addComponent(notGate);
        circuit.addComponent(bulb);

        circuit.addConnection(0, sw, 0, notGate); // Switch Out(0) to Not In(0)

        circuit.addConnection(0, notGate, 0, bulb); // Not Out(0) to Bulb In(0)

        sw.setState(false);
        circuit.simulate(); // Propagates signals
        assertTrue(
            bulb.isOn(),
            "Bulb should be ON when Switch is OFF through NOT gate"
        );

        sw.setState(true);
        circuit.simulate();
        assertFalse(
            bulb.isOn(),
            "Bulb should be OFF when Switch is ON through NOT gate"
        );
    }

    @Test
    void truthTableAnalysis() {
        Circuit circuit = new Circuit();
        Switch sw1 = new Switch();
        Switch sw2 = new Switch();
        And andGate = new And();
        Bulb bulb = new Bulb();

        circuit.addComponent(sw1);
        circuit.addComponent(sw2);
        circuit.addComponent(andGate);
        circuit.addComponent(bulb);

        circuit.addConnection(0, sw1, 0, andGate);
        circuit.addConnection(0, sw2, 1, andGate);
        circuit.addConnection(0, andGate, 0, bulb);

        // 2 inputs = 4 rows. Columns: SW1, SW2, Bulb
        boolean[][] truthTable = circuit.analyze();

        assertEquals(4, truthTable.length); // 2^2 combinations
        assertEquals(3, truthTable[0].length); // 2 Inputs + 1 Output

        // 0 0 -> 0
        assertFalse(truthTable[0][2]);
        // 0 1 -> 0
        assertFalse(truthTable[1][2]);
        // 1 0 -> 0
        assertFalse(truthTable[2][2]);
        // 1 1 -> 1
        assertTrue(truthTable[3][2]);
    }
}
