package com.logisim.domain.components;

public class Not extends Component {

    public Not() {
        name = "NotGate";
        inputs = new boolean[1];
        outputs = new boolean[1];
        positionX = 100;
        positionY = 100;
        inputs[0] = false;
        outputs[0] = true;
    }

    public void setInput(boolean value) {
        super.setInput(0, value);
    }

    public boolean getOutput() {
        return super.getOutput(0);
    }

    @Override
    public void execute() {
        if (inputs[0] == false) {
            outputs[0] = true;
        } else outputs[0] = false;
    }
}
