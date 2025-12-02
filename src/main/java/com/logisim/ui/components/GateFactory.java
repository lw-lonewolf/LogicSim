package com.logisim.ui.components;

import com.logisim.domain.components.Bulb;
import com.logisim.domain.components.Component;
import com.logisim.domain.components.SubCircuitComponent;
import com.logisim.domain.components.Switch;
import com.logisim.ui.controllers.GridController;
import com.logisim.ui.logic.ConnectionManager;
import java.util.function.Consumer;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

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
        Component component,
        Consumer<StackPane> onDeleteAction,
        Consumer<StackPane> onToggleAction
    ) {
        String imagePath;
        if (component instanceof SubCircuitComponent) {
            return createSubCircuitVisual(
                x,
                y,
                (SubCircuitComponent) component,
                canvasPane,
                gridController,
                onDeleteAction
            );
        }
        if (component instanceof Switch) {
            imagePath = "/com/logisim/ui/images/switch_off.png";

            if (((com.logisim.domain.components.Switch) component).isOn()) {
                imagePath = "/com/logisim/ui/images/switch_on.png";
            }
        } else if (component instanceof Bulb) {
            imagePath = "/com/logisim/ui/images/bulb_off.png";

            if (((com.logisim.domain.components.Bulb) component).isOn()) {
                imagePath = "/com/logisim/ui/images/bulb_on.png";
            }
        } else {
            imagePath =
                "/com/logisim/ui/images/" + gateName.toLowerCase() + ".png";
        }

        Image image = new Image(
            GateFactory.class.getResourceAsStream(imagePath)
        );
        ImageView imageView = new ImageView(image);
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(GATE_VISUAL_SIZE);
        imageView.setFitHeight(GATE_VISUAL_SIZE);
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
        makeDraggableandDeletable(
            stack,
            canvasPane,
            gridController,
            onDeleteAction
        );
        if (component instanceof Switch) {
            setupSwitchInteraction(
                stack,
                imageView,
                (Switch) component,
                onToggleAction
            );
        }
        return stack;
    }

    private static StackPane createSubCircuitVisual(
        double x,
        double y,
        com.logisim.domain.components.SubCircuitComponent subComp,
        Pane canvasPane,
        GridController gridController,
        java.util.function.Consumer<StackPane> onDeleteAction
    ) {
        int inputs = subComp.getInputs().length;
        int outputs = subComp.getOutputs().length;

        int maxPins = Math.max(inputs, outputs);
        double pinSpacing = 20.0;
        double height = Math.max(50, maxPins * pinSpacing + 20);
        double width = 80.0;

        Rectangle body = new Rectangle(width, height);
        body.setFill(Color.WHITE);
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(2);

        Text label = new Text(subComp.getName());
        label.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        StackPane stack = new StackPane(body, label);
        stack.setLayoutX(x);
        stack.setLayoutY(y);
        stack.setUserData(subComp);

        double startY = -(height / 2) + 10;

        for (int i = 0; i < inputs; i++) {
            Port p = new Port(true, stack, i);
            p.setTranslateX(-(width / 2));
            p.setTranslateY(startY + (i * pinSpacing));
            configurePortEvents(p);
            stack.getChildren().add(p);
        }

        for (int i = 0; i < outputs; i++) {
            Port p = new Port(false, stack, i);
            p.setTranslateX((width / 2));
            p.setTranslateY(startY + (i * pinSpacing));
            configurePortEvents(p);
            stack.getChildren().add(p);
        }

        makeDraggableandDeletable(
            stack,
            canvasPane,
            gridController,
            onDeleteAction
        );
        return stack;
    }

    private static void setupSwitchInteraction(
        StackPane stack,
        ImageView view,
        Switch switchLogic,
        Consumer<StackPane> onToggleAction
    ) {
        stack.setOnMouseClicked(e -> {
            if (e.isStillSincePress()) {
                switchLogic.toggle();
            }

            String newImage = switchLogic.isOn()
                ? "switch_on.png"
                : "switch_off.png";

            String path = "/com/logisim/ui/images/" + newImage;
            view.setImage(
                new Image(GateFactory.class.getResourceAsStream(path))
            );
            System.out.println("Switch toggled:" + switchLogic.isOn());
            if (onToggleAction != null) {
                onToggleAction.accept(stack);
            }
        });
    }

    private static void addPortsToGate(StackPane stack) {
        Component comp = (Component) stack.getUserData();
        int inputCount = comp.getInputs().length;
        int outputCount = comp.getOutputs().length;

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
        for (int i = 0; i < outputCount; i++) {
            Port outputPort = new Port(false, stack, i);
            outputPort.setTranslateX(halfSize + portExtension);
            outputPort.setTranslateY(0);

            configurePortEvents(outputPort);
            stack.getChildren().add(outputPort);
        }
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
        GridController gridController,
        Consumer<StackPane> onDeleteAction
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

            Component comp = (Component) node.getUserData();
            if (comp != null) {
                comp.setPositionX(newX);
                comp.setPositionY(newY);
            }

            event.consume();
        });

        node.setOnContextMenuRequested(event -> {
            ContextMenu menu = new ContextMenu();
            menu.setPrefHeight(10);
            menu.setPrefWidth(20);

            MenuItem deleteItem = new MenuItem("Delete Item");
            deleteItem.setOnAction(ev -> {
                if (onDeleteAction != null) {
                    onDeleteAction.accept(node);
                }
            });

            menu.getItems().add(deleteItem);
            menu.show(node, event.getScreenX(), event.getScreenY());
            event.consume();
        });
    }

    public static void refreshComponentState(StackPane visualGate) {
        Component comp = (Component) visualGate.getUserData();
        if (
            !(comp instanceof com.logisim.domain.components.Switch) &&
            !(comp instanceof com.logisim.domain.components.Bulb)
        ) {
            return;
        }

        ImageView view = (ImageView) visualGate.getChildren().get(1);

        if (comp instanceof Bulb) {
            Bulb bulb = (Bulb) comp;
            String imgName = bulb.isOn() ? "bulb_on.png" : "bulb_off.png";
            String path = "/com/logisim/ui/images/" + imgName;
            view.setImage(
                new Image(GateFactory.class.getResourceAsStream(path))
            );
        } else if (comp instanceof Switch) {
            Switch sw = (Switch) comp;
            String imgName = sw.isOn() ? "switch_on.png" : "switch_off.png";
            String path = "/com/logisim/ui/images/" + imgName;
            view.setImage(
                new Image(GateFactory.class.getResourceAsStream(path))
            );
        }
    }
}
