package com.logisim.domain.components;

import java.util.UUID;

public abstract class Component {

    protected String name;
    protected boolean[] inputs;
    protected boolean[] outputs;
    protected double positionX;
    protected double positionY;

    private String uuid;

    public abstract void execute();

    public Component() {
        this.uuid = UUID.randomUUID().toString();
    }

    public void setInput(int index, boolean value) {
        inputs[index] = value;
    }

    public boolean getOutput(int index) {
        return outputs[index];
    }

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

    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
