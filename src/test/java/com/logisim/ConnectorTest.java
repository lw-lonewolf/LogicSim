package com.logisim;

import static org.junit.jupiter.api.Assertions.*;

import com.logisim.domain.Connector;
import com.logisim.domain.components.Bulb;
import com.logisim.domain.components.Switch;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    @Test
    void signalPropagation() {
        Switch source = new Switch();
        Bulb sink = new Bulb();

        Connector connector = new Connector(source, sink, 0, 0);

        source.setState(true);
        source.execute();
        connector.process();
        sink.execute();

        assertTrue(
            sink.isOn(),
            "Bulb should be on when Switch is on and connected"
        );

        // Test Low Signal
        source.setState(false);
        source.execute();
        connector.process();
        sink.execute();

        assertFalse(sink.isOn(), "Bulb should be off when Switch is off");
    }
}
