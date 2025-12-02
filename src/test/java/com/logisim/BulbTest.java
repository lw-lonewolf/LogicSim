package com.logisim;

import static org.junit.jupiter.api.Assertions.*;

import com.logisim.domain.components.Bulb;
import org.junit.jupiter.api.Test;

class BulbTest {

    @Test
    void turnOn() {
        Bulb bulb = new Bulb();
        bulb.setInput(0, true);
        bulb.execute();
        assertTrue(bulb.isOn());
    }

    @Test
    void turnOff() {
        Bulb bulb = new Bulb();
        bulb.setInput(0, false);
        bulb.execute();
        assertFalse(bulb.isOn());
    }
}
