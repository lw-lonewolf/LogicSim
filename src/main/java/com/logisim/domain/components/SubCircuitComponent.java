package com.logisim.domain.components;

import com.logisim.domain.Circuit;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a complex circuit encapsulated as a single component within another circuit.
 * <p>
 * This class allows for hierarchical circuit design by treating an entire {@link Circuit}
 * as a "black box" component.
 * </p>
 * <p>
 * Mapping Logic:
 * <ul>
 *   <li><b>Inputs:</b> {@link Switch} components inside the inner circuit act as input pins for this component.</li>
 *   <li><b>Outputs:</b> {@link Bulb} components inside the inner circuit act as output pins for this component.</li>
 * </ul>
 * </p>
 */
public class SubCircuitComponent extends Component {

    /**
     * The underlying circuit logic being wrapped by this component.
     */
    private Circuit innerCircuit;

    /**
     * The unique identifier of the source circuit in the database.
     */
    private long sourceCircuitId;

    /**
     * The list of switches within the inner circuit, serving as input interfaces.
     */
    private List<Switch> internalSwitches;

    /**
     * The list of bulbs within the inner circuit, serving as output interfaces.
     */
    private List<Bulb> internalBulbs;

    /**
     * Constructs a new SubCircuitComponent based on an existing circuit.
     * <p>
     * This constructor scans the provided circuit for {@link Switch} and {@link Bulb} components.
     * The size of the component's input array is determined by the number of switches,
     * and the size of the output array is determined by the number of bulbs found.
     * </p>
     *
     * @param loadedCircuit The fully loaded {@link Circuit} object to be encapsulated.
     */
    public SubCircuitComponent(Circuit loadedCircuit) {
        super();
        this.innerCircuit = loadedCircuit;
        this.sourceCircuitId = loadedCircuit.getId();

        this.internalSwitches = new ArrayList<>();
        this.internalBulbs = new ArrayList<>();

        for (Component c : innerCircuit.getComponents()) {
            if (c instanceof Switch) {
                internalSwitches.add((Switch) c);
            } else if (c instanceof Bulb) {
                internalBulbs.add((Bulb) c);
            }
        }

        this.inputs = new boolean[internalSwitches.size()];
        this.outputs = new boolean[internalBulbs.size()];
    }

    /**
     * Executes the logic of the encapsulated circuit.
     * <p>
     * This method performs three main steps:
     * 1. Maps the values from this component's input pins to the internal switches of the inner circuit.
     * 2. Simulates the inner circuit. It runs a loop proportional to the number of components
     *    to ensure signals propagate through the internal logic gates.
     * 3. Maps the resulting states of the internal bulbs to this component's output pins.
     * </p>
     */
    @Override
    public void execute() {
        for (int i = 0; i < inputs.length; i++) {
            internalSwitches.get(i).setState(inputs[i]);
        }

        for (int i = 0; i < innerCircuit.getComponents().size() + 2; i++) {
            innerCircuit.simulate();
        }

        for (int i = 0; i < outputs.length; i++) {
            outputs[i] = internalBulbs.get(i).isOn();
        }
    }

    /**
     * Retrieves the name of the component, which corresponds to the name of the inner circuit.
     *
     * @return The name of the encapsulated circuit.
     */
    @Override
    public String getName() {
        return innerCircuit.getName();
    }

    /**
     * Gets the ID of the circuit used as the source for this component.
     *
     * @return The database ID of the inner circuit.
     */
    public long getSourceCircuitId() {
        return sourceCircuitId;
    }

    /**
     * Retrieves the actual {@link Circuit} object being simulated internally.
     *
     * @return The inner {@link Circuit} instance.
     */
    public Circuit getInnerCircuit() {
        return innerCircuit;
    }

    /**
     * Sets the inner circuit logic.
     *
     * @param innerCircuit The new {@link Circuit} to encapsulate.
     */
    public void setInnerCircuit(Circuit innerCircuit) {
        this.innerCircuit = innerCircuit;
    }

    /**
     * Sets the source circuit ID.
     *
     * @param sourceCircuitId The database ID of the circuit.
     */
    public void setSourceCircuitId(long sourceCircuitId) {
        this.sourceCircuitId = sourceCircuitId;
    }

    /**
     * Retrieves the list of switches inside the inner circuit that act as inputs.
     *
     * @return A list of {@link Switch} components.
     */
    public List<Switch> getInternalSwitches() {
        return internalSwitches;
    }

    /**
     * Sets the list of internal switches.
     *
     * @param internalSwitches The list of {@link Switch} components.
     */
    public void setInternalSwitches(List<Switch> internalSwitches) {
        this.internalSwitches = internalSwitches;
    }

    /**
     * Retrieves the list of bulbs inside the inner circuit that act as outputs.
     *
     * @return A list of {@link Bulb} components.
     */
    public List<Bulb> getInternalBulbs() {
        return internalBulbs;
    }

    /**
     * Sets the list of internal bulbs.
     *
     * @param internalBulbs The list of {@link Bulb} components.
     */
    public void setInternalBulbs(List<Bulb> internalBulbs) {
        this.internalBulbs = internalBulbs;
    }
}
