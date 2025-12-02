package com.logisim.ui.components;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;

/**
 * Represents a visual wire connection between two {@link Port}s on the circuit canvas.
 * <p>
 * This class extends {@link Polyline} to render a physical line representing the logic
 * flow between components. It implements an orthogonal (Manhattan-style) routing algorithm
 * to draw lines with right angles. The wire automatically updates its geometry whenever
 * the connected components are moved.
 * </p>
 */
public class Wire extends Polyline {

    private final Port source;
    private final Port sink;

    /**
     * Constructs a new Wire connecting a source port to a sink port.
     * <p>
     * This constructor initializes the wire's visual properties (color, width),
     * attaches layout listeners to the parent components of the ports to handle movement,
     * and sets up user interactions.
     * </p>
     *
     * @param source The {@link Port} acting as the signal source.
     * @param sink   The {@link Port} acting as the signal sink.
     */
    public Wire(Port source, Port sink) {
        this.source = source;
        this.sink = sink;

        setStroke(Color.BLUE);
        setStrokeWidth(4);
        setOpacity(0.8);
        setFill(Color.TRANSPARENT);

        StackPane sourceGate = source.getParentGate();
        StackPane sinkGate = sink.getParentGate();

        sourceGate.layoutXProperty().addListener(o -> updateWire());
        sourceGate.layoutYProperty().addListener(o -> updateWire());

        sinkGate.layoutXProperty().addListener(o -> updateWire());
        sinkGate.layoutYProperty().addListener(o -> updateWire());

        Platform.runLater(this::updateWire);

        setupInteractions();
    }

    /**
     * Recalculates the geometric path of the wire based on the current positions of the ports.
     * <p>
     * This method computes the absolute coordinates of the source and sink ports relative
     * to their parent container. It then generates a 4-point path:
     * <ol>
     *   <li>Start point (Source coordinates).</li>
     *   <li>Midpoint 1 (Horizontal movement to the midpoint between X coordinates).</li>
     *   <li>Midpoint 2 (Vertical movement to the target Y coordinate).</li>
     *   <li>End point (Sink coordinates).</li>
     * </ol>
     * </p>
     */
    private void updateWire() {
        StackPane sourceGate = source.getParentGate();
        StackPane sinkGate = sink.getParentGate();

        double startX =
            sourceGate.getLayoutX() + source.getBoundsInParent().getCenterX();
        double startY =
            sourceGate.getLayoutY() + source.getBoundsInParent().getCenterY();

        double endX =
            sinkGate.getLayoutX() + sink.getBoundsInParent().getCenterX();
        double endY =
            sinkGate.getLayoutY() + sink.getBoundsInParent().getCenterY();

        double midX = (startX + endX) / 2;

        ObservableList<Double> points = getPoints();
        points.clear();

        points.addAll(startX, startY, midX, startY, midX, endY, endX, endY);
    }

    /**
     * Configures mouse interactions for the wire.
     * <p>
     * Adds handlers for:
     * <ul>
     *   <li><b>Hover:</b> Increases stroke width to indicate focus.</li>
     *   <li><b>Context Menu:</b> Provides options to change the wire color (Red, Blue, Green, etc.) or delete the wire.</li>
     * </ul>
     * </p>
     */
    private void setupInteractions() {
        setOnMouseEntered(e -> {
            setStrokeWidth(6);
        });

        setOnMouseExited(e -> {
            setStrokeWidth(4);
        });

        ContextMenu menu = new ContextMenu();
        MenuItem colorRed = new MenuItem("Color: Red");
        colorRed.setOnAction(e -> setStroke(Color.RED));

        MenuItem colorBlue = new MenuItem("Color: Blue");
        colorBlue.setOnAction(e -> setStroke(Color.BLUE));

        MenuItem colorGreen = new MenuItem("Color: Green");
        colorGreen.setOnAction(e -> setStroke(Color.GREEN));

        MenuItem colorWhite = new MenuItem("Color: White");
        colorWhite.setOnAction(e -> setStroke(Color.WHITE));

        MenuItem colorOrange = new MenuItem("Color: Orange");
        colorOrange.setOnAction(e -> setStroke(Color.ORANGE));

        MenuItem deleteWire = new MenuItem("Delete Wire");
        deleteWire.setOnAction(e -> {
            if (getParent() != null) {
                this.sink.setConnectionState(false);
                ((Pane) getParent()).getChildren().remove(this);
                // Connector c = (Connector) this.getUserData();
            }
        });

        menu
            .getItems()
            .addAll(
                colorRed,
                colorGreen,
                colorBlue,
                colorWhite,
                colorOrange,
                deleteWire
            );
        setOnContextMenuRequested(event -> {
            menu.show(this, event.getScreenX(), event.getScreenY());
            event.consume();
        });
    }

    /**
     * Retrieves the source port of this wire.
     *
     * @return The {@link Port} where the signal originates.
     */
    public Port getSource() {
        return source;
    }

    /**
     * Retrieves the sink port of this wire.
     *
     * @return The {@link Port} where the signal terminates.
     */
    public Port getSink() {
        return sink;
    }
}
