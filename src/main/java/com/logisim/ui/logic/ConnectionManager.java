package com.logisim.ui.logic;

import com.logisim.domain.Connector;
import com.logisim.domain.components.Component;
import com.logisim.ui.components.Port;
import com.logisim.ui.components.Wire;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class ConnectionManager {

    private Port selectedSourcePort = null;
    private Pane canvasPane;
    private Consumer<Connector> onConnectionAdded;

    private Line interactionLine;

    public ConnectionManager(Pane canvasPane) {
        this.canvasPane = canvasPane;
    }

    public void setOnConnectionAdded(Consumer<Connector> listener) {
        this.onConnectionAdded = listener;
    }

    public void onMouseMove(MouseEvent event) {
        if (selectedSourcePort != null && interactionLine != null) {
            interactionLine.setEndX(event.getX());
            interactionLine.setEndY(event.getY());
        }
    }

    public void cancelConnection() {
        if (selectedSourcePort != null) {
            selectedSourcePort.setSelected(false);
            selectedSourcePort = null;

            if (interactionLine != null) {
                canvasPane.getChildren().remove(interactionLine);
                interactionLine = null;
            }
            System.out.println("Connection Canceled.");
        }
    }

    public void handlePortClick(Port clickedPort) {
        System.out.println(
            "Clicked Port type: " + (clickedPort.isInput() ? "INPUT" : "OUTPUT")
        );

        if (selectedSourcePort == null) {
            if (clickedPort.isInput() == false) {
                selectedSourcePort = clickedPort;
                selectedSourcePort.setSelected(true);

                interactionLine = new Line();
                interactionLine.setStroke(Color.GRAY);
                interactionLine.setStrokeWidth(2);
                interactionLine.getStrokeDashArray().addAll(10d, 10d);
                interactionLine.setMouseTransparent(true);

                StackPane gate = clickedPort.getParentGate();
                interactionLine
                    .startXProperty()
                    .bind(
                        gate
                            .layoutXProperty()
                            .add(
                                Bindings.createDoubleBinding(
                                    () ->
                                        clickedPort
                                            .getBoundsInParent()
                                            .getCenterX(),
                                    clickedPort.boundsInParentProperty()
                                )
                            )
                    );
                interactionLine
                    .startYProperty()
                    .bind(
                        gate
                            .layoutYProperty()
                            .add(
                                Bindings.createDoubleBinding(
                                    () ->
                                        clickedPort
                                            .getBoundsInParent()
                                            .getCenterY(),
                                    clickedPort.boundsInParentProperty()
                                )
                            )
                    );

                interactionLine.setEndX(
                    gate.getLayoutX() + clickedPort.getTranslateX()
                );
                interactionLine.setEndY(
                    gate.getLayoutY() + clickedPort.getTranslateY()
                );
                canvasPane.getChildren().add(interactionLine);
                System.out.println(">>> Connection STARTED from Output.");
            } else {
                System.out.println(
                    "X Cannot start connection from an Input port."
                );
            }
        } else {
            if (
                clickedPort.isInput() &&
                clickedPort.getParentGate() !=
                selectedSourcePort.getParentGate()
            ) {
                if (
                    clickedPort.isInput() &&
                    clickedPort.getParentGate() !=
                    selectedSourcePort.getParentGate()
                ) {
                    if (!clickedPort.getConnectionState()) {
                        createConnection(selectedSourcePort, clickedPort);

                        canvasPane.getChildren().remove(interactionLine);
                        interactionLine = null;

                        selectedSourcePort.setSelected(false);
                        selectedSourcePort = null;

                        clickedPort.setConnectionState(true);
                    } else {
                        System.out.println("Input port already occupied.");
                        cancelConnection();
                    }
                } else {
                    System.out.println(
                        "Invalid connection (Must be Output -> Input)."
                    );
                    cancelConnection();
                }
            }
        }
    }

    private void createConnection(Port source, Port sink) {
        Wire wire = new Wire(source, sink);
        canvasPane.getChildren().add(wire);
        wire.toBack();

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
