package com.logisim.domain.components;

import java.util.UUID;

/**
 * An abstract base class representing a generic component within a logic circuit.
 * <p>
 * This class provides the foundational structure for all specific logic gates and components
 * (e.g., AND, OR, Switch). It manages common properties such as the component's unique identifier,
 * its visual position on the canvas, and the state of its input and output pins.
 * </p>
 */
public abstract class Component {

    /**
     * The name or type identifier of the component (e.g., "and", "or").
     */
    protected String name;

    /**
     * An array representing the state of the component's input pins.
     * {@code true} represents a high signal (1), and {@code false} represents a low signal (0).
     */
    protected boolean[] inputs;

    /**
     * An array representing the state of the component's output pins.
     * This is typically updated by the {@link #execute()} method.
     */
    protected boolean[] outputs;

    /**
     * The X-coordinate of the component's position in the visual interface.
     */
    protected double positionX;

    /**
     * The Y-coordinate of the component's position in the visual interface.
     */
    protected double positionY;

    /**
     * A unique identifier (UUID) assigned to this specific component instance.
     * Used for identifying the component during persistence and connection mapping.
     */
    private String uuid;

    /**
     * Default constructor.
     * <p>
     * Initializes the component with a newly generated UUID.
     * </p>
     */
    public Component() {
        this.uuid = UUID.randomUUID().toString();
    }

    /**
     * Constructs a component with a specified name.
     * <p>
     * Initializes the component with the given name and a newly generated UUID.
     * </p>
     *
     * @param name The name or type identifier for this component.
     */
    public Component(String name) {
        this.name = name;
        this.uuid = UUID.randomUUID().toString();
    }

    /**
     * Executes the logical operation of the component.
     * <p>
     * Implementation classes must define this method to update the {@code outputs} array
     * based on the current values in the {@code inputs} array.
     * </p>
     */
    public abstract void execute();

    /**
     * Sets the state of a specific input pin.
     *
     * @param index The index of the input pin to update.
     * @param value The new boolean state (high/low) for the pin.
     */
    public void setInput(int index, boolean value) {
        inputs[index] = value;
    }

    /**
     * Retrieves the state of a specific output pin.
     *
     * @param index The index of the output pin to read.
     * @return {@code true} if the pin is high, {@code false} if low.
     */
    public boolean getOutput(int index) {
        return outputs[index];
    }

    /**
     * Gets the name of the component.
     *
     * @return The string identifier of the component.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the component.
     *
     * @param name The new name or type identifier.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the entire array of input pin states.
     *
     * @return A boolean array representing all inputs.
     */
    public boolean[] getInputs() {
        return inputs;
    }

    /**
     * Sets the entire array of input pin states.
     *
     * @param inputs The boolean array to replace the current inputs.
     */
    public void setInputs(boolean[] inputs) {
        this.inputs = inputs;
    }

    /**
     * Retrieves the entire array of output pin states.
     *
     * @return A boolean array representing all outputs.
     */
    public boolean[] getOutputs() {
        return outputs;
    }

    /**
     * Sets the entire array of output pin states.
     *
     * @param outputs The boolean array to replace the current outputs.
     */
    public void setOutputs(boolean[] outputs) {
        this.outputs = outputs;
    }

    /**
     * Gets the X-coordinate of the component.
     *
     * @return The horizontal position.
     */
    public double getPositionX() {
        return positionX;
    }

    /**
     * Gets the Y-coordinate of the component.
     *
     * @return The vertical position.
     */
    public double getPositionY() {
        return positionY;
    }

    /**
     * Sets the X-coordinate of the component.
     *
     * @param positionX The new horizontal position.
     */
    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    /**
     * Sets the Y-coordinate of the component.
     *
     * @param positionY The new vertical position.
     */
    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    /**
     * Gets the unique identifier (UUID) of the component.
     *
     * @return The UUID string.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the unique identifier (UUID) of the component.
     * <p>
     * This is typically used when reloading a component from the database to restore its original ID.
     * </p>
     *
     * @param uuid The UUID string to assign.
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
