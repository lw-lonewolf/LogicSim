package com.logisim.ui.components;

import com.logisim.domain.components.Component;
import com.logisim.ui.controllers.GridController;
import com.logisim.ui.logic.ConnectionManager;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GateFactory {

    private static ConnectionManager connectionManager;
    private static final double GATE_VISUAL_SIZE = 100.0;

    public static void setConnectionManager(ConnectionManager cm) {
        connectionManager = cm;
    }

    private static class Delta {

        double x, y;
    }

    public static StackPane createGateWithHitBox(
        String gateName,
        double x,
        double y,
        Pane canvasPane,
        GridController gridController,
        Component component
    ) {
        ImageView imageView = createGate(gateName, x, y);
        Rectangle hitbox = new Rectangle(
            imageView.getFitWidth(),
            imageView.getFitHeight()
        );
        hitbox.setFill(Color.TRANSPARENT);
        StackPane stack = new StackPane(hitbox, imageView);
        stack.setLayoutX(x);
        stack.setLayoutY(y);
        stack.setUserData(component);
        addPortsToGate(stack);
        makeDraggableandDeletable(stack, canvasPane, gridController);
        return stack;
    }

    private static ImageView createGate(String gateName, double x, double y) {
        String path = "/com/logisim/ui/images/" + gateName + ".png";
        Image image = new Image(GateFactory.class.getResourceAsStream(path));
        ImageView imageView = new ImageView(image);
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(GATE_VISUAL_SIZE);
        imageView.setFitHeight(GATE_VISUAL_SIZE);
        return imageView;
    }

    private static void addPortsToGate(StackPane stack) {
        Component comp = (Component) stack.getUserData();
        int inputCount = comp.getInputs().length;

        double halfSize = GATE_VISUAL_SIZE / 2.0;

        double portExtension = 5.0;
        double xOffsetInput = -(halfSize + portExtension);

        double portSpacing = 20.0;

        for (int i = 0; i < inputCount; i++) {
            Port inputPort = new Port(true, stack, i);
            inputPort.setTranslateX(xOffsetInput);

            double yoffset = (i - (inputCount - 1) / 2.0) * portSpacing;
            inputPort.setTranslateY(yoffset);

            configurePortEvents(inputPort);
            stack.getChildren().add(inputPort);
        }

        Port outputPort = new Port(false, stack, 0);

        outputPort.setTranslateX(halfSize + portExtension);
        outputPort.setTranslateY(0);

        configurePortEvents(outputPort);
        stack.getChildren().add(outputPort);
    }

    private static void configurePortEvents(Port port) {
        port.setOnMouseClicked(e -> {
            if (connectionManager != null) {
                connectionManager.handlePortClick(port);
                e.consume();
            }
        });
    }

    private static void makeDraggableandDeletable(
        StackPane node,
        Pane canvasPane,
        GridController gridController
    ) {
        final Delta dragDelta = new Delta();

        node.setOnMousePressed(event -> {
            dragDelta.x = node.getLayoutX() - event.getSceneX();
            dragDelta.y = node.getLayoutY() - event.getSceneY();
        });

        node.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                event.consume();

                double X = event.getSceneX() + dragDelta.x;
                double Y = event.getSceneY() + dragDelta.y;

                node.setLayoutX(X);
                node.setLayoutY(Y);
            }
        });

        node.setOnMouseReleased(event -> {
            double newX =
                Math.round(node.getLayoutX() / gridController.getGridSize()) *
                gridController.getGridSize();
            double newY =
                Math.round(node.getLayoutY() / gridController.getGridSize()) *
                gridController.getGridSize();
            node.setLayoutX(newX);
            node.setLayoutY(newY);
            event.consume();
        });

        node.setOnContextMenuRequested(event -> {
            ContextMenu menu = new ContextMenu();
            menu.setPrefHeight(10);
            menu.setPrefWidth(20);

            MenuItem deleteItem = new MenuItem("Delete Item");
            deleteItem.setOnAction(ev -> {
                canvasPane.getChildren().remove(node);
                Component comp = (Component) node.getUserData();
                if (comp != null) {
                    //TODO: add //circuit.removeComponent(comp)
                }
            });

            menu.getItems().add(deleteItem);
            menu.show(node, event.getScreenX(), event.getScreenY());
            event.consume();
        });
    }
}
