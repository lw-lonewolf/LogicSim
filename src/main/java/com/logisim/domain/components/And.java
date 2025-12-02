package com.logisim.domain.components;

/**
 * Represents a logical AND gate component within the circuit simulation.
 * <p>
 * This component accepts two boolean inputs and produces a single boolean output.
 * The output is true if and only if both inputs are true; otherwise, the output is false.
 * </p>
 */
public class And extends Component {

    /**
     * Constructs a new AND gate component with default settings.
     * <p>
     * Initializes the component with:
     * <ul>
     *   <li>Name: "and"</li>
     *   <li>Input array size: 2</li>
     *   <li>Output array size: 1</li>
     *   <li>Default position: (100, 100)</li>
     *   <li>Initial state: All inputs and outputs set to false.</li>
     * </ul>
     * </p>
     */
    public And() {
        name = "and";
        inputs = new boolean[2];
        outputs = new boolean[1];
        positionX = 100;
        positionY = 100;
        inputs[0] = false;
        inputs[1] = false;
        outputs[0] = false;
    }

    /**
     * Retrieves the current state of the AND gate's output pin.
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
     * Executes the logic of the AND gate.
     * <p>
     * This method updates the internal output state based on the current values
     * of the input pins. The output at index 0 is set to {@code true} only if
     * both input index 0 and input index 1 are {@code true}.
     * </p>
     */
    @Override
    public void execute() {
        if (inputs[0] && inputs[1]) {
            outputs[0] = true;
        } else outputs[0] = false;
    }
}
