package com.logisim.domain.components;

public class Bulb extends Component {

    private boolean isOn;

    public Bulb() {
        super();
        this.inputs = new boolean[1];
        this.outputs = new boolean[0];
        this.isOn = false;
    }

    @Override
    public void execute() {
        this.isOn = inputs[0];
    }

    public boolean isOn() {
        return isOn;
    }

    @Override
    public String getName() {
        return "bulb";
    }
}
