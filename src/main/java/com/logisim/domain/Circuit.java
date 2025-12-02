package com.logisim.domain;

import com.logisim.domain.components.Component;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class Circuit {

    private long id;
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

    public void removeComponent(Component comp) {
        if (comp == null) return;
        connectors.removeIf(
            c -> c.getSourceComp() == comp || c.getSinkComp() == comp
        );
        components.remove(comp);
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public void setConnectors(List<Connector> connectors) {
        this.connectors = connectors;
    }

    @Override
    public String toString() {
        return name;
    }
}
