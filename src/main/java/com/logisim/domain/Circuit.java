package com.logisim.domain;

import com.logisim.domain.components.Component;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class Circuit {

    private String name;
    private List<Component> components = new ArrayList<>();
    private List<Connector> connectors = new ArrayList<>();

    public Circuit() {
        name = "Circuit";
    }

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

    public void removeComponent(int index) throws InvalidParameterException {
        if (index < 0 || index >= components.size()) {
            throw new InvalidParameterException("Invalid Index Occured.");
        }

        Component toBeRemoved = components.get(index);

        connectors.removeIf(
            connector ->
                connector.sourceComp == toBeRemoved ||
                connector.sinkComp == toBeRemoved
        );

        components.remove(index);
    }

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
                source >= components.get(sourceComp).getInputs().length) ||
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

    public List<Component> getComponents() {
        return components;
    }

    public String getName() {
        return name;
    }

    public List<Connector> getConnectors() {
        return connectors;
    }

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

    public boolean[][] analyze() {
        List<Component> inputComponents = new ArrayList<>();
        List<Integer> inputIndices = new ArrayList<>();
        for (Component comp : components) {
            for (int i = 0; i < comp.getInputs().length; i++) {
                boolean connectedAsSink = false;
                for (Connector conn : connectors) {
                    if (conn.sinkComp == comp && conn.sink == i) {
                        connectedAsSink = true;
                        break;
                    }
                }
                if (!connectedAsSink) {
                    inputComponents.add(comp);
                    inputIndices.add(i);
                }
            }
        }

        List<Component> outputComponents = new ArrayList<>();
        List<Integer> outputIndices = new ArrayList<>();
        for (Component comp : components) {
            for (int i = 0; i < comp.getOutputs().length; i++) {
                boolean connectedAsSource = false;
                for (Connector conn : connectors) {
                    if (conn.sourceComp == comp && conn.source == i) {
                        connectedAsSource = true;
                        break;
                    }
                }
                if (!connectedAsSource) {
                    outputComponents.add(comp);
                    outputIndices.add(i);
                }
            }
        }

        int nInputs = inputComponents.size();
        int nOutputs = outputComponents.size();
        int totalCombinations = 1 << nInputs; // 2^nInputs

        boolean[][] truthTable = new boolean[totalCombinations][nInputs +
        nOutputs];

        for (int row = 0; row < totalCombinations; row++) {
            int remainder = row;

            for (int col = nInputs - 1; col >= 0; col--) {
                boolean value = (remainder % 2 == 1);
                remainder /= 2;

                inputComponents.get(col).setInput(inputIndices.get(col), value);
                truthTable[row][col] = value;
            }

            simulate();

            for (int col = 0; col < nOutputs; col++) {
                truthTable[row][nInputs + col] = outputComponents
                    .get(col)
                    .getOutput(outputIndices.get(col));
            }
        }

        return truthTable;
    }

    public String generateBooleanExpression(
        boolean[][] truthTable,
        List<String> inputNames
    ) {
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
}
