package com.logisim.ui.components;

import com.logisim.domain.Connector;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;

public class Wire extends Polyline {

    private final Port source;
    private final Port sink;

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
                //TODO: add logic to rmeove this connector from circuit
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

    public Port getSource() {
        return source;
    }

    public Port getSink() {
        return sink;
    }
}
