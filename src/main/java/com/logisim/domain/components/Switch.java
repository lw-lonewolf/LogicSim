package com.logisim.domain.components;

/**
 * Represents a toggle switch component in the circuit simulation.
 * <p>
 * A Switch acts as a primary input source for a circuit. It has no input pins
 * but provides a single output pin. The output signal is determined by the
 * internal state of the switch (On/High or Off/Low).
 * </p>
 */
public class Switch extends Component {

    /**
     * The internal state of the switch.
     * {@code true} indicates the switch is closed (On), generating a high signal.
     * {@code false} indicates the switch is open (Off), generating a low signal.
     */
    private boolean isOn;

    /**
     * Constructs a new Switch component.
     * <p>
     * Initializes the component with:
     * <ul>
     *   <li>Name: "switch"</li>
     *   <li>Input array size: 0 (Switches do not receive signals from other components)</li>
     *   <li>Output array size: 1</li>
     *   <li>Initial state: Off ({@code false})</li>
     * </ul>
     * </p>
     */
    public Switch() {
        super("switch");
        isOn = false;
        this.inputs = new boolean[0];
        this.outputs = new boolean[1];
        this.outputs[0] = false;
    }

    /**
     * Checks if the switch is currently in the "On" state.
     *
     * @return {@code true} if the switch is on, {@code false} otherwise.
     */
    public boolean isOn() {
        return isOn;
    }

    /**
     * Updates the component's output pin based on its internal state.
     * <p>
     * If the internal state {@code isOn} is true, the output at index 0 becomes true.
     * Otherwise, it becomes false.
     * </p>
     */
    @Override
    public void execute() {
        this.outputs[0] = isOn;
    }

    /**
     * Toggles the current state of the switch.
     * <p>
     * If the switch is On, it turns Off. If it is Off, it turns On.
     * After changing the state, {@link #execute()} is called immediately to update the output pin.
     * </p>
     */
    public void toggle() {
        this.isOn = !this.isOn;
        execute();
    }

    /**
     * Explicitly sets the state of the switch.
     * <p>
     * This method is useful for programmatically controlling the switch, such as when
     * it is used as an input interface for a {@link SubCircuitComponent}.
     * Triggers {@link #execute()} to update the output pin.
     * </p>
     *
     * @param state The new state to apply ({@code true} for On, {@code false} for Off).
     */
    public void setState(boolean state) {
        this.isOn = state;
        execute();
    }

    /**
     * Retrieves the unique type name of this component.
     *
     * @return The string literal "switch".
     */
    @Override
    public String getName() {
        return "switch";
    }
}
