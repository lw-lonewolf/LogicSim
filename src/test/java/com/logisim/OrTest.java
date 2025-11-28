package com.logisim;

import static org.junit.jupiter.api.Assertions.*;

import com.logisim.domain.components.Or;
import org.junit.jupiter.api.Test;

class OrTest {

    @Test
    void truetrueInput() {
        Or OrGate = new Or();
        OrGate.setInput(0, true);
        OrGate.setInput(1, true);
        OrGate.execute();
        assertTrue(OrGate.getOutput());
    }

    @Test
    void falsefalseInput() {
        Or OrGate = new Or();
        OrGate.setInput(0, false);
        OrGate.setInput(1, false);
        OrGate.execute();
        assertFalse(OrGate.getOutput());
    }

    @Test
    void falsetrueInput() {
        Or OrGate = new Or();
        OrGate.setInput(0, false);
        OrGate.setInput(1, true);
        OrGate.execute();
        assertTrue(OrGate.getOutput());
    }

    @Test
    void truefalseInput() {
        Or OrGate = new Or();
        OrGate.setInput(0, true);
        OrGate.setInput(1, false);
        OrGate.execute();
        assertTrue(OrGate.getOutput());
    }
}
