package com.logisim;

import static org.junit.jupiter.api.Assertions.*;

import com.logisim.business.Not;
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
}
