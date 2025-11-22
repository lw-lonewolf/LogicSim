package com.logisim.business;

public abstract class Component {

    protected String name;
    protected boolean[] inputs;
    protected boolean[] outputs;
    protected int positionX;
    protected int positionY;

    public void setInput(int index, boolean value) {
        inputs[index] = value;
    }

    public boolean getOutput(int index) {
        return outputs[index];
    }

    public abstract void execute();
}
