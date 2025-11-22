package com.logisim.business;

public class Not extends Component {

    public Not() {
        name = "NotGate";
        inputs = new boolean[1];
        outputs = new boolean[1];
        positionX = 0;
        positionY = 0;
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
