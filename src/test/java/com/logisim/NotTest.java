package com.logisim;

import static org.junit.jupiter.api.Assertions.*;

import com.logisim.domain.components.Not;
import org.junit.jupiter.api.Test;

class NotTest {

    @Test
    void trueInput() {
        Not notGate = new Not();
        notGate.setInput(true);
        notGate.execute();
        assertFalse(notGate.getOutput());
    }

    @Test
    void falseInput() {
        Not notGate = new Not();
        notGate.setInput(false);
        notGate.execute();
        assertTrue(notGate.getOutput());
    }

    @Test
    void initialState() {
        // Default constructor sets input[0] to false, so output should be true
        Not notGate = new Not();
        assertTrue(notGate.getOutput());
    }
}
