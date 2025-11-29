package com.logisim.domain.components;

public class Or extends Component {

    public Or() {
        name = "or";
        inputs = new boolean[2];
        outputs = new boolean[1];
        positionX = 100;
        positionY = 100;
        inputs[0] = false;
        inputs[1] = false;
        outputs[0] = false;
    }

    public boolean getOutput() {
        return super.getOutput(0);
    }

    @Override
    public void execute() {
        if (inputs[0] || inputs[1]) {
            outputs[0] = true;
        } else outputs[0] = false;
    }
}
