package com.logisim;

import static org.junit.jupiter.api.Assertions.*;

import com.logisim.domain.components.And;
import com.logisim.ui.components.Port;
import com.logisim.ui.logic.ConnectionManager;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ConnectionManagerTest {

    @BeforeAll
    static void initJfx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {}
    }

    @Test
    void testCancelConnection() {
        Pane canvas = new Pane();
        ConnectionManager cm = new ConnectionManager(canvas);
        StackPane gate = new StackPane();
        Port port = new Port(false, gate, 0); // Output port

        cm.handlePortClick(port);
        assertTrue(port.isSelected());

        cm.cancelConnection();
        assertFalse(port.isSelected());
    }

    @Test
    void testInvalidInputToInputConnection() {
        Pane canvas = new Pane();
        ConnectionManager cm = new ConnectionManager(canvas);

        StackPane gate1 = new StackPane();

        Port input1 = new Port(true, gate1, 0);

        // Click Input first (Invalid start)
        cm.handlePortClick(input1);
        assertFalse(input1.isSelected());

        // Select Output then Input (Valid Sequence)
        Port output = new Port(false, gate1, 0);
        cm.handlePortClick(output);
        assertTrue(output.isSelected());
    }

    @Test
    void testSuccessfulConnection() {
        Pane canvas = new Pane();
        ConnectionManager cm = new ConnectionManager(canvas);

        StackPane gate1 = new StackPane();
        gate1.setUserData(new And());
        StackPane gate2 = new StackPane();
        gate2.setUserData(new And());

        Port output = new Port(false, gate1, 0);
        Port input = new Port(true, gate2, 0);

        final boolean[] callbackCalled = { false };
        cm.setOnConnectionAdded(c -> callbackCalled[0] = true);

        cm.handlePortClick(output); // Start
        cm.handlePortClick(input); // End

        assertFalse(output.isSelected());
        assertTrue(input.getConnectionState());
        assertTrue(callbackCalled[0]);
    }
}
