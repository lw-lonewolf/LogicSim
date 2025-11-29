package com.logisim.domain.components;

public class Switch extends Component {

    private boolean isOn;

    public Switch() {
        super("switch");
        isOn = false;
        this.inputs = new boolean[0];
        this.outputs = new boolean[1];
        this.outputs[0] = false;
    }

    public boolean isOn() {
        return isOn;
    }

    @Override
    public void execute() {
        this.outputs[0] = isOn;
    }

    public void toggle() {
        this.isOn = !this.isOn;
        execute();
    }

    public void setState(boolean state) {
        this.isOn = state;
        execute();
    }

    @Override
    public String getName() {
        return "switch";
    }
}
