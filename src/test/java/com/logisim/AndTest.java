package com.logisim;

import static org.junit.jupiter.api.Assertions.*;

import com.logisim.domain.components.And;
import org.junit.jupiter.api.Test;

class AndTest {

    @Test
    void truetrueInput() {
        And andGate = new And();
        andGate.setInput(0, true);
        andGate.setInput(1, true);
        andGate.execute();
        assertTrue(andGate.getOutput());
    }

    @Test
    void falsefalseInput() {
        And andGate = new And();
        andGate.setInput(0, false);
        andGate.setInput(1, false);
        andGate.execute();
        assertFalse(andGate.getOutput());
    }

    @Test
    void falsetrueInput() {
        And andGate = new And();
        andGate.setInput(0, false);
        andGate.setInput(1, true);
        andGate.execute();
        assertFalse(andGate.getOutput());
    }

    @Test
    void truefalseInput() {
        And andGate = new And();
        andGate.setInput(0, true);
        andGate.setInput(1, false);
        andGate.execute();
        assertFalse(andGate.getOutput());
    }
}
