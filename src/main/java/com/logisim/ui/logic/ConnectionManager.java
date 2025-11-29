package com.logisim.ui.logic;

import com.logisim.domain.Connector;
import com.logisim.domain.components.Component;
import com.logisim.ui.components.Port;
import com.logisim.ui.components.Wire;
import java.util.function.Consumer;
import javafx.scene.layout.Pane;

public class ConnectionManager {

    private Port selectedSourcePort = null;
    private Pane canvasPane;
    private Consumer<Connector> onConnectionAdded;

    public void setOnConnectionAdded(Consumer<Connector> listener) {
        this.onConnectionAdded = listener;
    }

    public ConnectionManager(Pane canvasPane) {
        this.canvasPane = canvasPane;
    }

    public void handlePortClick(Port clickedPort) {
        System.out.println(
            "Clicked Port type: " + (clickedPort.isInput() ? "INPUT" : "OUTPUT")
        );

        if (selectedSourcePort == null) {
            if (clickedPort.isInput() == false) {
                selectedSourcePort = clickedPort;
                selectedSourcePort.setSelected(true);
                System.out.println(">>> Connection STARTED from Output.");
            } else {
                System.out.println(
                    "X Cannot start connection from an Input port."
                );
            }
        } else {
            if (clickedPort.isInput() == true) {
                if (clickedPort.getConnectionState()) {
                    System.out.println(
                        "Connection Failed: Input Port is in use."
                    );
                    cancelSelection();
                    return;
                }

                if (
                    clickedPort.getParentGate() !=
                    selectedSourcePort.getParentGate()
                ) {
                    createConnection(selectedSourcePort, clickedPort);
                    clickedPort.setConnectionState(true);

                    selectedSourcePort.setSelected(false);
                    selectedSourcePort = null;
                    System.out.println(">>> Connection CREATED successfully.");
                } else {
                    System.out.println("X Cannot connect a gate to itself.");
                    cancelSelection();
                }
            } else {
                System.out.println(
                    "X Connection Failed: You must connect Output to Input."
                );
                cancelSelection();
            }
        }
    }

    private void cancelSelection() {
        if (selectedSourcePort != null) {
            selectedSourcePort.setSelected(false);
            selectedSourcePort = null;
            System.out.println(">>> Selection CANCELED.");
        }
    }

    private void createConnection(Port source, Port sink) {
        Wire wire = new Wire(source, sink);
        canvasPane.getChildren().add(wire);

        Component sourceComp = (Component) source.getParentGate().getUserData();
        Component sinkComp = (Component) sink.getParentGate().getUserData();
        Connector c = new Connector(
            sourceComp,
            sinkComp,
            source.getIndex(),
            sink.getIndex()
        );
        wire.setUserData(c);
        if (onConnectionAdded != null) {
            onConnectionAdded.accept(c);
        }
        System.out.println(
            "Connection Created. + sent to Circuit as a listener var"
        );
    }
}
