package com.logisim;

import static org.junit.jupiter.api.Assertions.*;

import com.logisim.domain.components.Switch;
import org.junit.jupiter.api.Test;

class SwitchTest {

    @Test
    void initialOffState() {
        Switch sw = new Switch();
        sw.execute();
        assertFalse(sw.isOn());
        assertFalse(sw.getOutput(0));
    }

    @Test
    void toggleState() {
        Switch sw = new Switch();
        sw.toggle(); // Turns On
        assertTrue(sw.isOn());
        assertTrue(sw.getOutput(0));

        sw.toggle(); // Turns Off
        assertFalse(sw.isOn());
        assertFalse(sw.getOutput(0));
    }

    @Test
    void setStateExplicit() {
        Switch sw = new Switch();
        sw.setState(true);
        assertTrue(sw.isOn());
        assertTrue(sw.getOutput(0));

        sw.setState(false);
        assertFalse(sw.isOn());
    }
}
