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

/**
 * Manages the interactive state for creating connections (wires) between components.
 * <p>
 * This class handles the two-step process of wiring: selecting a source output port
 * and then selecting a destination input port. It provides visual feedback via a
 * temporary interaction line that follows the mouse cursor and handles the validation
 * logic to ensure connections are valid (e.g., Output to Input, no self-loops).
 * </p>
 */
public class ConnectionManager {

    /**
     * The port selected as the starting point (Source/Output) of the connection.
     * If {@code null}, no connection is currently being created.
     */
    private Port selectedSourcePort = null;

    /**
     * The visual pane where wires and interaction lines are drawn.
     */
    private Pane canvasPane;

    /**
     * Callback listener that is triggered when a connection is successfully finalized.
     * This allows the logical circuit model to be updated.
     */
    private Consumer<Connector> onConnectionAdded;

    /**
     * A temporary line used to visualize the wire being dragged by the user before
     * the connection is finalized.
     */
    private Line interactionLine;

    /**
     * Constructs a new ConnectionManager.
     *
     * @param canvasPane The {@link Pane} used as the drawing surface for the circuit.
     */
    public ConnectionManager(Pane canvasPane) {
        this.canvasPane = canvasPane;
    }

    /**
     * Sets the callback listener to be executed when a connection is successfully created.
     *
     * @param listener A {@link Consumer} that accepts a {@link Connector} object.
     */
    public void setOnConnectionAdded(Consumer<Connector> listener) {
        this.onConnectionAdded = listener;
    }

    /**
     * Updates the temporary interaction line to follow the mouse cursor.
     * <p>
     * This method should be called when the mouse moves over the canvas. If a connection
     * is in progress, the end of the line snaps to the current mouse coordinates.
     * </p>
     *
     * @param event The {@link MouseEvent} containing the current cursor position.
     */
    public void onMouseMove(MouseEvent event) {
        if (selectedSourcePort != null && interactionLine != null) {
            interactionLine.setEndX(event.getX());
            interactionLine.setEndY(event.getY());
        }
    }

    /**
     * Cancels the current connection attempt.
     * <p>
     * Resets the internal state by deselecting the source port and removing the
     * temporary interaction line from the canvas.
     * </p>
     */
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

    /**
     * Handles click events on ports to initiate or finalize a connection.
     * <p>
     * The logic flows as follows:
     * <ol>
     *   <li><b>No Selection Active:</b> If the clicked port is an OUTPUT, it is selected as the source,
     *       and a temporary line is drawn. Inputs are ignored.</li>
     *   <li><b>Source Selected:</b> If the clicked port is an INPUT, resides on a different gate,
     *       and is not already connected, the connection is finalized. Otherwise, the operation is canceled.</li>
     * </ol>
     * </p>
     *
     * @param clickedPort The {@link Port} that was clicked by the user.
     */
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

    /**
     * Finalizes the creation of a connection between two ports.
     * <p>
     * This method:
     * 1. Creates the visual {@link Wire} and adds it to the canvas.
     * 2. Creates the logical {@link Connector} object mapping component indices.
     * 3. Triggers the {@code onConnectionAdded} callback to update the circuit model.
     * </p>
     *
     * @param source The source (output) port.
     * @param sink   The sink (input) port.
     */
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
