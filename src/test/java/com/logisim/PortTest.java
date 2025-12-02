package com.logisim;

import static org.junit.jupiter.api.Assertions.*;

import com.logisim.ui.components.Port;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PortTest {

    @BeforeAll
    static void initJfx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {}
    }

    @Test
    void testInitialization() {
        StackPane parent = new StackPane();
        Port port = new Port(true, parent, 0);

        assertTrue(port.isInput());
        assertEquals(parent, port.getParentGate());
        assertEquals(0, port.getIndex());
        assertEquals(Color.TRANSPARENT, port.getFill());
        assertEquals(Color.BLUE, port.getStroke());
    }

    @Test
    void testSelection() {
        StackPane parent = new StackPane();
        Port port = new Port(false, parent, 1);

        assertFalse(port.isSelected());
        port.setSelected(true);
        assertTrue(port.isSelected());
    }

    @Test
    void testConnectionState() {
        StackPane parent = new StackPane();
        Port port = new Port(true, parent, 0);

        assertFalse(port.getConnectionState());
        port.setConnectionState(true);
        assertTrue(port.getConnectionState());
    }
}
