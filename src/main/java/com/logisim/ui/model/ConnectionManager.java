package com.logisim.ui.model;

import com.logisim.business.Component;
import com.logisim.business.Connector;
import java.sql.Connection;
import javafx.scene.layout.Pane;

public class ConnectionManager {

    private Port selectedSourcePort = null;
    private Pane canvasPane;

    public ConnectionManager(Pane canvasPane) {
        this.canvasPane = canvasPane;
    }

    public void handlePortClick(Port clickedPort) {
        // DEBUG: Print what we just clicked
        System.out.println(
            "Clicked Port type: " + (clickedPort.isInput() ? "INPUT" : "OUTPUT")
        );

        // CASE 1: NO SELECTION YET (We want to start a wire)
        if (selectedSourcePort == null) {
            // LOGIC: A wire must start from an OUTPUT (Source)
            if (clickedPort.isInput() == false) {
                selectedSourcePort = clickedPort;
                selectedSourcePort.setSelected(true);
                System.out.println(">>> Connection STARTED from Output.");
            } else {
                System.out.println(
                    "X Cannot start connection from an Input port."
                );
            }
        }
        // CASE 2: SOURCE SELECTED (We want to finish the wire)
        else {
            // LOGIC: A wire must end at an INPUT (Sink)
            if (clickedPort.isInput() == true) {
                // VALIDATION: Cannot connect to the exact same gate (loops)
                if (
                    clickedPort.getParentGate() !=
                    selectedSourcePort.getParentGate()
                ) {
                    createConnection(selectedSourcePort, clickedPort);

                    // SUCCESS: Clear selection
                    selectedSourcePort.setSelected(false);
                    selectedSourcePort = null;
                    System.out.println(">>> Connection CREATED successfully.");
                } else {
                    System.out.println("X Cannot connect a gate to itself.");
                    cancelSelection();
                }
            } else {
                // User clicked Output -> Output. This is invalid.
                System.out.println(
                    "X Connection Failed: You must connect Output to Input."
                );
                // Optional: Cancel selection if they click wrong
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
        Component sinkComp = (Component) source.getParentGate().getUserData();
        Connector c = new Connector(
            sourceComp,
            sinkComp,
            source.getIndex(),
            sink.getIndex()
        );
        wire.setUserData(c);
        //TODO : LOGIC MAPPING tO WORK WITH CIRCUIT TO BE ADDED HERE
        // c.process(
        System.out.println("Connection Created.");
    }
}
