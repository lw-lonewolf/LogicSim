package com.logisim.domain.components;

/**
 * Represents a logical NOT gate (inverter) in the circuit simulation.
 * <p>
 * A NOT gate implements logical negation. It has a single input and a single output.
 * The output is always the inverse of the input (i.e., if input is true, output is false;
 * if input is false, output is true).
 * </p>
 */
public class Not extends Component {

    /**
     * Constructs a new NOT gate component with default settings.
     * <p>
     * Initializes the component with:
     * <ul>
     *   <li>Name: "not"</li>
     *   <li>Input array size: 1</li>
     *   <li>Output array size: 1</li>
     *   <li>Default position: (100, 100)</li>
     *   <li>Initial state: Input set to false, resulting in Output set to true.</li>
     * </ul>
     * </p>
     */
    public Not() {
        name = "not";
        inputs = new boolean[1];
        outputs = new boolean[1];
        positionX = 100;
        positionY = 100;
        inputs[0] = false;
        outputs[0] = true;
    }

    /**
     * Sets the state of the component's single input pin.
     * <p>
     * This is a convenience wrapper around {@link Component#setInput(int, boolean)}
     * targeting index 0.
     * </p>
     *
     * @param value The boolean value to apply to the input.
     */
    public void setInput(boolean value) {
        super.setInput(0, value);
    }

    /**
     * Retrieves the state of the component's single output pin.
     * <p>
     * This is a convenience wrapper around {@link Component#getOutput(int)}
     * targeting index 0.
     * </p>
     *
     * @return {@code true} if the output is high, {@code false} otherwise.
     */
    public boolean getOutput() {
        return super.getOutput(0);
    }

    /**
     * Executes the logical operation of the NOT gate.
     * <p>
     * This method inverts the value found at input index 0 and stores the result
     * at output index 0.
     * </p>
     */
    @Override
    public void execute() {
        if (inputs[0] == false) {
            outputs[0] = true;
        } else outputs[0] = false;
    }
}
