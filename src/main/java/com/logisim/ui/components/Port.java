package com.logisim.ui.components;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Represents a specific connection point (pin) on a visual logic component.
 * <p>
 * A Port acts as an interface for creating wire connections between components.
 * It is visually represented as a small circle. It maintains information about
 * whether it is an input or output, its index within the component's logical
 * structure, and its parent visual component.
 * </p>
 */
public class Port extends Circle {

    /**
     * Indicates the direction of the port.
     * {@code true} if this is an input port; {@code false} if it is an output port.
     */
    private boolean isInput;

    /**
     * Indicates whether this port is currently selected by the user
     * (e.g., during the process of creating a connection).
     */
    private boolean selected;

    /**
     * Represents the active state or signal value associated with this port.
     */
    private boolean connectionState = false;

    /**
     * The index of this port corresponding to the component's internal
     * input or output arrays.
     */
    private int index;

    /**
     * The visual container (StackPane) of the component to which this port belongs.
     */
    private StackPane parentGate;

    /**
     * Constructs a new Port instance.
     * <p>
     * Initializes the port's visual style (transparent fill, blue stroke) and
     * sets up mouse hover effects to provide visual feedback (turning yellow on hover).
     * </p>
     *
     * @param isInput    {@code true} if this is an input pin, {@code false} for output.
     * @param parentGate The visual {@link StackPane} of the owning component.
     * @param index      The logical index of this pin on the component.
     */
    public Port(boolean isInput, StackPane parentGate, int index) {
        super(5);
        this.isInput = isInput;
        this.selected = false;
        this.parentGate = parentGate;
        this.index = index;
        setFill(Color.TRANSPARENT);
        setStroke(Color.BLUE);
        setStrokeWidth(2);

        setOnMouseEntered(e -> setFill(Color.YELLOW));
        setOnMouseExited(e -> setFill(Color.TRANSPARENT));
    }

    /**
     * Checks if this port is an input.
     *
     * @return {@code true} if input, {@code false} if output.
     */
    public boolean isInput() {
        return isInput;
    }

    /**
     * Retrieves the current connection state or signal value of the port.
     *
     * @return The boolean state of the connection.
     */
    public boolean getConnectionState() {
        return connectionState;
    }

    /**
     * Sets the connection state or signal value for this port.
     *
     * @param value The new boolean state.
     */
    public void setConnectionState(boolean value) {
        connectionState = value;
    }

    /**
     * Sets the selection status of the port.
     * <p>
     * Used by the connection manager to highlight ports during wiring.
     * </p>
     *
     * @param value {@code true} to mark as selected, {@code false} otherwise.
     */
    public void setSelected(boolean value) {
        selected = value;
    }

    /**
     * Retrieves the visual component that owns this port.
     *
     * @return The parent {@link StackPane}.
     */
    public StackPane getParentGate() {
        return parentGate;
    }

    /**
     * Checks if the port is currently selected.
     *
     * @return {@code true} if selected, {@code false} otherwise.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Retrieves the logical index of this port.
     * <p>
     * This index corresponds to the {@code inputs[]} or {@code outputs[]} array
     * in the underlying logic component.
     * </p>
     *
     * @return The integer index.
     */
    public int getIndex() {
        return index;
    }
}
