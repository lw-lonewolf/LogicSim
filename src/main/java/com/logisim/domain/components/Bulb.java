package com.logisim.domain.components;

/**
 * Represents a light bulb component in the circuit simulation.
 * <p>
 * The Bulb serves as a visual output device. It accepts a single input signal.
 * When the input signal is high (true), the bulb is considered "on" or illuminated.
 * When the input signal is low (false), the bulb is "off".
 * </p>
 */
public class Bulb extends Component {

    /**
     * Internal state indicating whether the bulb is currently illuminated.
     */
    private boolean isOn;

    /**
     * Constructs a new Bulb component.
     * <p>
     * Initializes the component with:
     * <ul>
     *   <li>1 Input pin.</li>
     *   <li>0 Output pins.</li>
     *   <li>Initial state set to off ({@code false}).</li>
     * </ul>
     * </p>
     */
    public Bulb() {
        super();
        this.inputs = new boolean[1];
        this.outputs = new boolean[0];
        this.isOn = false;
    }

    /**
     * Updates the state of the bulb based on the current input.
     * <p>
     * This method reads the value from the first input pin (index 0)
     * and updates the internal {@code isOn} state to match it.
     * </p>
     */
    @Override
    public void execute() {
        this.isOn = inputs[0];
    }

    /**
     * Checks if the bulb is currently illuminated.
     *
     * @return {@code true} if the bulb is on (receiving a high signal), {@code false} otherwise.
     */
    public boolean isOn() {
        return isOn;
    }

    /**
     * Retrieves the unique type name of this component.
     *
     * @return The string literal "bulb".
     */
    @Override
    public String getName() {
        return "bulb";
    }
}
