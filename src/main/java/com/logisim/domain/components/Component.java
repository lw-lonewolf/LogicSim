package com.logisim.domain.components;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean[] getInputs() {
        return inputs;
    }

    public void setInputs(boolean[] inputs) {
        this.inputs = inputs;
    }

    public boolean[] getOutputs() {
        return outputs;
    }

    public void setOutputs(boolean[] outputs) {
        this.outputs = outputs;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }
}
