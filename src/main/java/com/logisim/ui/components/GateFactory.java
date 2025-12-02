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

/**
 * A factory class responsible for creating and configuring the visual representations
 * of logic components (gates, switches, bulbs, etc.) on the UI canvas.
 * <p>
 * This class handles:
 * <ul>
 *   <li>Loading the appropriate images for components.</li>
 *   <li>Creating dynamic visual structures for sub-circuits.</li>
 *   <li>Attaching input/output {@link Port}s to components.</li>
 *   <li>Setting up event handlers for dragging, clicking, and context menus.</li>
 * </ul>
 * </p>
 */
public class GateFactory {

    private static ConnectionManager connectionManager;
    private static final double GATE_VISUAL_SIZE = 100.0;

    /**
     * Sets the {@link ConnectionManager} used to handle port interactions.
     *
     * @param cm The connection manager instance.
     */
    public static void setConnectionManager(ConnectionManager cm) {
        connectionManager = cm;
    }

    /**
     * Helper class to store the offset during drag operations.
     */
    private static class Delta {

        double x, y;
    }

    /**
     * Creates a visual component (StackPane) representing a logic gate or device.
     * <p>
     * This is the main entry point for adding components to the UI. It determines the specific
     * type of component (Standard Gate, Switch, Bulb, or SubCircuit), creates the visual elements,
     * attaches ports, and configures user interactions (dragging, toggling, deleting).
     * </p>
     *
     * @param gateName       The string identifier for the gate type (used for image loading).
     * @param x              The initial X coordinate on the canvas.
     * @param y              The initial Y coordinate on the canvas.
     * @param canvasPane     The parent pane where this component will be added (used for drag bounds/calculations).
     * @param gridController The controller used for snapping the component to the grid.
     * @param component      The underlying logical {@link Component} object.
     * @param onDeleteAction A callback function to execute when the delete context menu item is clicked.
     * @param onToggleAction A callback function to execute when a switch is toggled (can be null for non-switches).
     * @return A {@link StackPane} containing the visual elements of the component.
     */
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

    /**
     * Creates the specific visual representation for a {@link SubCircuitComponent}.
     * <p>
     * Unlike standard gates which use static images, sub-circuits are drawn dynamically
     * as a rectangle with text labels and a variable number of input/output pins.
     * </p>
     *
     * @param x              The initial X coordinate.
     * @param y              The initial Y coordinate.
     * @param subComp        The sub-circuit logical component.
     * @param canvasPane     The canvas pane.
     * @param gridController The grid controller for snapping.
     * @param onDeleteAction The callback for deletion.
     * @return A {@link StackPane} representing the sub-circuit.
     */
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

    /**
     * Configures the mouse interaction for {@link Switch} components.
     * <p>
     * Sets up a click handler that toggles the logical state of the switch, updates
     * the visual image (on/off), and triggers the provided callback.
     * </p>
     *
     * @param stack          The visual container of the switch.
     * @param view           The ImageView to update.
     * @param switchLogic    The underlying switch logical component.
     * @param onToggleAction The callback to execute after toggling.
     */
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

    /**
     * Dynamically adds {@link Port} objects to a component's visual stack.
     * <p>
     * This method calculates the physical positions of input pins (left side) and
     * output pins (right side) based on the number of inputs/outputs defined in
     * the component's logic.
     * </p>
     *
     * @param stack The component's visual container.
     */
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

    /**
     * Binds mouse click events on ports to the {@link ConnectionManager}.
     *
     * @param port The port to configure.
     */
    private static void configurePortEvents(Port port) {
        port.setOnMouseClicked(e -> {
            if (connectionManager != null) {
                connectionManager.handlePortClick(port);
                e.consume();
            }
        });
    }

    /**
     * Adds drag-and-drop functionality and a context menu for deletion to the component.
     * <p>
     * Dragging snaps the component to the grid defined by the {@link GridController}.
     * The context menu allows removing the component via the {@code onDeleteAction}.
     * </p>
     *
     * @param node           The visual component to make interactive.
     * @param canvasPane     The pane containing the component.
     * @param gridController The controller for grid snapping calculations.
     * @param onDeleteAction The callback to execute on deletion.
     */
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

    /**
     * Updates the visual state of a component (Switch or Bulb) based on its logical state.
     * <p>
     * This is typically called during or after circuit simulation to reflect signal changes
     * (e.g., turning a bulb on or off).
     * </p>
     *
     * @param visualGate The visual {@link StackPane} of the component.
     */
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
