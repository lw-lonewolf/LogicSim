package com.logisim;

import static org.junit.jupiter.api.Assertions.*;

import com.logisim.domain.components.Or;
import org.junit.jupiter.api.Test;

class OrTest {

    @Test
    void truetrueInput() {
        Or orGate = new Or();
        orGate.setInput(0, true);
        orGate.setInput(1, true);
        orGate.execute();
        assertTrue(orGate.getOutput());
    }

    @Test
    void falsefalseInput() {
        Or orGate = new Or();
        orGate.setInput(0, false);
        orGate.setInput(1, false);
        orGate.execute();
        assertFalse(orGate.getOutput());
    }

    @Test
    void falsetrueInput() {
        Or orGate = new Or();
        orGate.setInput(0, false);
        orGate.setInput(1, true);
        orGate.execute();
        assertTrue(orGate.getOutput());
    }

    @Test
    void truefalseInput() {
        Or orGate = new Or();
        orGate.setInput(0, true);
        orGate.setInput(1, false);
        orGate.execute();
        assertTrue(orGate.getOutput());
    }
}
