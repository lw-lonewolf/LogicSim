package com.logisim.domain;

import com.logisim.domain.components.Component;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a digital logic circuit consisting of components and connections.
 * <p>
 * This class serves as the container for logic simulation. It manages a list of
 * {@link Component} objects and {@link Connector} objects (wires). It provides
 * functionality to modify the circuit structure, simulate the flow of signals
 * through the components, and analyze the circuit's logic via truth tables and
 * boolean expressions.
 * </p>
 */
public class Circuit {

    /**
     * The unique identifier for this circuit, typically used for database persistence.
     */
    private long id;

    /**
     * The name of the circuit.
     */
    private String name;

    /**
     * The list of logic components (gates, switches, bulbs, etc.) contained in this circuit.
     */
    private List<Component> components = new ArrayList<>();

    /**
     * The list of connections (wires) linking the components together.
     */
    private List<Connector> connectors = new ArrayList<>();

    /**
     * Constructs a new Circuit with a default name.
     */
    public Circuit() {
        name = "Circuit";
    }

    /**
     * Adds a component to the circuit.
     *
     * @param comp The {@link Component} to be added.
     * @throws IllegalArgumentException If the provided component is {@code null} or already exists in the circuit.
     */
    public void addComponent(Component comp) throws IllegalArgumentException {
        if (comp == null) {
            throw new IllegalArgumentException(
                "Expected Component but Recieved Null"
            );
        }
        for (Component component : components) {
            if (component == comp) {
                throw new IllegalArgumentException(
                    "Component Already Exists in Components."
                );
            }
        }
        components.add(comp);
    }

    /**
     * Removes a component from the circuit.
     * <p>
     * This method also automatically removes any {@link Connector}s attached to the
     * removed component to prevent dangling connections.
     * </p>
     *
     * @param comp The {@link Component} to be removed.
     */
    public void removeComponent(Component comp) {
        if (comp == null) return;
        connectors.removeIf(
            c -> c.getSourceComp() == comp || c.getSinkComp() == comp
        );
        components.remove(comp);
    }

    /**
     * Adds a connection between two components identified by their indices in the component list.
     *
     * @param source     The index of the output pin on the source component.
     * @param sourceComp The index of the source component in the internal components list.
     * @param sink       The index of the input pin on the sink (destination) component.
     * @param sinkComp   The index of the sink component in the internal components list.
     * @throws InvalidParameterException If the component indices are out of bounds or the pin indices are invalid for the respective components.
     */
    public void addConnection(
        int source,
        int sourceComp,
        int sink,
        int sinkComp
    ) {
        if (
            (sourceComp < 0 || sourceComp >= components.size()) ||
            (sinkComp < 0 || sinkComp >= components.size())
        ) {
            throw new InvalidParameterException(
                "Source or Sink Component Does Not Exist"
            );
        } else if (
            (source < 0 ||
                source >= components.get(sourceComp).getOutputs().length) ||
            (sink < 0 || sink >= components.get(sinkComp).getInputs().length)
        ) {
            throw new InvalidParameterException(
                "source pin or sink pin doesn't exist"
            );
        }
        Connector connector = new Connector(
            components.get(sourceComp),
            components.get(sinkComp),
            source,
            sink
        );
        connectors.add(connector);
    }

    /**
     * Adds a connection between two specific component instances.
     *
     * @param sourcePin  The index of the output pin on the source component.
     * @param sourceComp The source {@link Component} instance.
     * @param sinkPin    The index of the input pin on the sink component.
     * @param sinkComp   The sink {@link Component} instance.
     * @throws IllegalArgumentException If either component is null, or if the specified pin indices do not exist.
     */
    public void addConnection(
        int sourcePin,
        Component sourceComp,
        int sinkPin,
        Component sinkComp
    ) {
        if (sourceComp == null || sinkComp == null) {
            throw new IllegalArgumentException(
                "Source or Sink Component cannot be null"
            );
        }

        if (sourcePin < 0 || sourcePin >= sourceComp.getOutputs().length) {
            throw new IllegalArgumentException(
                "Source pin index " +
                    sourcePin +
                    " does not exist on component " +
                    sourceComp.getName()
            );
        }

        if (sinkPin < 0 || sinkPin >= sinkComp.getInputs().length) {
            throw new IllegalArgumentException(
                "Sink pin index " +
                    sinkPin +
                    " does not exist on component " +
                    sinkComp.getName()
            );
        }

        Connector connector = new Connector(
            sourceComp,
            sinkComp,
            sourcePin,
            sinkPin
        );
        connectors.add(connector);
    }

    /**
     * Simulates the circuit logic for one cycle.
     * <p>
     * This method executes every component (calculating outputs from inputs) and then
     * processes every connector (propagating outputs to inputs of connected components).
     * </p>
     */
    public void simulate() {
        for (Component comp : components) {
            comp.execute();

            for (Connector conn : connectors) {
                if (conn.sourceComp == comp) {
                    conn.process();
                }
            }
        }
    }

    /**
     * Generates a truth table for the current circuit configuration.
     * <p>
     * This method identifies all {@link com.logisim.domain.components.Switch} components as inputs
     * and all {@link com.logisim.domain.components.Bulb} components as outputs. It iterates through
     * all possible binary combinations of input states ($2^n$), simulates the circuit for each combination,
     * and records the resulting output states.
     * </p>
     *
     * @return A 2D boolean array representing the truth table.
     *         Rows represent each input combination.
     *         Columns [0 to nInputs-1] represent input values.
     *         Columns [nInputs to end] represent output values.
     *         Returns a 0x0 array if no inputs or outputs are found.
     */
    public boolean[][] analyze() {
        List<Component> switches = new ArrayList<>();
        List<Component> bulbs = new ArrayList<>();

        for (Component comp : components) {
            if (comp instanceof com.logisim.domain.components.Switch) {
                switches.add(comp);
            } else if (comp instanceof com.logisim.domain.components.Bulb) {
                bulbs.add(comp);
            }
        }

        int nInputs = switches.size();
        int nOutputs = bulbs.size();

        if (nInputs == 0 || nOutputs == 0) {
            return new boolean[0][0];
        }

        int totalCombinations = 1 << nInputs;
        boolean[][] truthTable = new boolean[totalCombinations][nInputs +
        nOutputs];

        for (int row = 0; row < totalCombinations; row++) {
            int tempRow = row;
            for (int col = nInputs - 1; col >= 0; col--) {
                boolean isOn = (tempRow % 2) == 1;
                tempRow /= 2;

                ((com.logisim.domain.components.Switch) switches.get(
                        col
                    )).setState(isOn);

                truthTable[row][col] = isOn;
            }

            for (int i = 0; i < components.size() + 2; i++) {
                simulate();
            }

            for (int col = 0; col < nOutputs; col++) {
                com.logisim.domain.components.Bulb bulb =
                    (com.logisim.domain.components.Bulb) bulbs.get(col);
                truthTable[row][nInputs + col] = bulb.isOn();
            }
        }

        return truthTable;
    }

    /**
     * Generates a boolean algebraic expression (Sum of Products) based on the provided truth table.
     * <p>
     * The method constructs a string expression representing the logic required to produce
     * a 'true' output. It creates minterms for every row in the truth table where the output
     * is true.
     * </p>
     *
     * @param truthTable A 2D boolean array generated by {@link #analyze()}.
     * @param inputNames A list of names corresponding to the input columns in the truth table.
     * @return A String representing the boolean expression (e.g., "(A & B) + (!A & B)").
     *         Returns "0" if the output is never true.
     */
    public String generateBooleanExpression(
        boolean[][] truthTable,
        List<String> inputNames
    ) {
        for (int i = 0; i < truthTable.length; i++) {
            for (int j = 0; j < truthTable[0].length; j++) {
                System.out.print(truthTable[i][j]);
            }
            System.out.print("\n");
        }
        StringBuilder expression = new StringBuilder();
        int nRows = truthTable.length;
        int nInputs = inputNames.size();
        int outputCol = nInputs;
        for (int row = 0; row < nRows; row++) {
            if (truthTable[row][outputCol]) {
                if (expression.length() > 0) expression.append(" + ");
                expression.append("(");
                for (int col = 0; col < nInputs; col++) {
                    if (col > 0) expression.append(" & ");
                    if (truthTable[row][col]) {
                        expression.append(inputNames.get(col));
                    } else {
                        expression.append("!" + inputNames.get(col));
                    }
                }
                expression.append(")");
            }
        }
        if (expression.length() == 0) {
            return "0";
        }
        return expression.toString();
    }

    /**
     * Retrieves the list of components in the circuit.
     *
     * @return The list of {@link Component} objects.
     */
    public List<Component> getComponents() {
        return components;
    }

    /**
     * Retrieves the name of the circuit.
     *
     * @return The circuit name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the list of connectors (wires) in the circuit.
     *
     * @return The list of {@link Connector} objects.
     */
    public List<Connector> getConnectors() {
        return connectors;
    }

    /**
     * Gets the unique identifier of the circuit.
     *
     * @return The circuit ID.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the circuit.
     *
     * @param id The new circuit ID.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Sets the name of the circuit.
     *
     * @param name The new circuit name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Replaces the current list of components with a new list.
     *
     * @param components The new list of {@link Component} objects.
     */
    public void setComponents(List<Component> components) {
        this.components = components;
    }

    /**
     * Replaces the current list of connectors with a new list.
     *
     * @param connectors The new list of {@link Connector} objects.
     */
    public void setConnectors(List<Connector> connectors) {
        this.connectors = connectors;
    }

    /**
     * Returns the string representation of the circuit.
     *
     * @return The name of the circuit.
     */
    @Override
    public String toString() {
        return name;
    }
}
