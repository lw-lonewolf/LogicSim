package com.logisim.domain.components;

/**
 * Represents a logical OR gate component within the circuit simulation.
 * <p>
 * This component accepts two boolean inputs and produces a single boolean output.
 * The output is true if at least one of the inputs is true. It is false only if
 * both inputs are false.
 * </p>
 */
public class Or extends Component {

    /**
     * Constructs a new OR gate component with default settings.
     * <p>
     * Initializes the component with:
     * <ul>
     *   <li>Name: "or"</li>
     *   <li>Input array size: 2</li>
     *   <li>Output array size: 1</li>
     *   <li>Default position: (100, 100)</li>
     *   <li>Initial state: All inputs and outputs set to false.</li>
     * </ul>
     * </p>
     */
    public Or() {
        name = "or";
        inputs = new boolean[2];
        outputs = new boolean[1];
        positionX = 100;
        positionY = 100;
        inputs[0] = false;
        inputs[1] = false;
        outputs[0] = false;
    }

    /**
     * Retrieves the current state of the OR gate's output pin.
     * <p>
     * This is a convenience method that delegates to the superclass's
     * {@link Component#getOutput(int)} method with index 0.
     * </p>
     *
     * @return {@code true} if the gate is outputting a high signal, {@code false} otherwise.
     */
    public boolean getOutput() {
        return super.getOutput(0);
    }

    /**
     * Executes the logical operation of the OR gate.
     * <p>
     * This method updates the internal output state based on the current values
     * of the input pins. The output at index 0 is set to {@code true} if
     * either input index 0 or input index 1 (or both) are {@code true}.
     * </p>
     */
    @Override
    public void execute() {
        if (inputs[0] || inputs[1]) {
            outputs[0] = true;
        } else outputs[0] = false;
    }
}
