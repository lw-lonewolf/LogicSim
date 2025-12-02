package com.logisim.domain.components;

import com.logisim.domain.Circuit;
import java.util.ArrayList;
import java.util.List;

public class SubCircuitComponent extends Component {

    private Circuit innerCircuit;
    private long sourceCircuitId;
    private List<Switch> internalSwitches;
    private List<Bulb> internalBulbs;

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

    @Override
    public String getName() {
        return innerCircuit.getName();
    }

    public long getSourceCircuitId() {
        return sourceCircuitId;
    }

    public Circuit getInnerCircuit() {
        return innerCircuit;
    }

    public void setInnerCircuit(Circuit innerCircuit) {
        this.innerCircuit = innerCircuit;
    }

    public void setSourceCircuitId(long sourceCircuitId) {
        this.sourceCircuitId = sourceCircuitId;
    }

    public List<Switch> getInternalSwitches() {
        return internalSwitches;
    }

    public void setInternalSwitches(List<Switch> internalSwitches) {
        this.internalSwitches = internalSwitches;
    }

    public List<Bulb> getInternalBulbs() {
        return internalBulbs;
    }

    public void setInternalBulbs(List<Bulb> internalBulbs) {
        this.internalBulbs = internalBulbs;
    }
}
