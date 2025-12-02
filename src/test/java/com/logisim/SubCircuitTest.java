package com.logisim;

import static org.junit.jupiter.api.Assertions.*;

import com.logisim.domain.Circuit;
import com.logisim.domain.components.Bulb;
import com.logisim.domain.components.Not;
import com.logisim.domain.components.SubCircuitComponent;
import com.logisim.domain.components.Switch;
import org.junit.jupiter.api.Test;

class SubCircuitComponentTest {

    @Test
    void initializationMapping() {
        Circuit inner = new Circuit();
        inner.setName("TestCircuit");

        inner.addComponent(new Switch());
        inner.addComponent(new Switch());
        inner.addComponent(new Bulb());

        SubCircuitComponent sub = new SubCircuitComponent(inner);

        assertEquals("TestCircuit", sub.getName());
        assertEquals(2, sub.getInputs().length);
        assertEquals(1, sub.getOutputs().length);
    }

    @Test
    void executePassthroughLogic() {
        Circuit inner = new Circuit();
        Switch sw = new Switch();
        Bulb bulb = new Bulb();

        inner.addComponent(sw);
        inner.addComponent(bulb);
        inner.addConnection(0, sw, 0, bulb);

        SubCircuitComponent sub = new SubCircuitComponent(inner);

        sub.setInput(0, true);
        sub.execute();
        assertTrue(sub.getOutput(0));

        sub.setInput(0, false);
        sub.execute();
        assertFalse(sub.getOutput(0));
    }

    @Test
    void executeInvertedLogic() {
        Circuit inner = new Circuit();
        Switch sw = new Switch();
        Not notGate = new Not();
        Bulb bulb = new Bulb();

        inner.addComponent(sw);
        inner.addComponent(notGate);
        inner.addComponent(bulb);

        inner.addConnection(0, sw, 0, notGate);
        inner.addConnection(0, notGate, 0, bulb);

        SubCircuitComponent sub = new SubCircuitComponent(inner);

        sub.setInput(0, true);
        sub.execute();
        assertFalse(sub.getOutput(0));

        sub.setInput(0, false);
        sub.execute();
        assertTrue(sub.getOutput(0));
    }
}
