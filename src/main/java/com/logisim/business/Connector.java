package com.logisim.business;

import java.awt.Color;

public class Connector {

    protected String name;
    protected Color color;
    protected int positionX;
    protected int positionY;
    protected int source;
    protected int sink;
    protected Component sourceComp;
    protected Component sinkComp;

    public Connector(
        Component sourceComp,
        Component sinkComp,
        int source,
        int sink
    ) {
        name = "Connector";
        color = Color.BLUE;
        positionX = 0;
        positionY = 0;
        this.sink = sink;
        this.source = source;
        this.sourceComp = sourceComp;
        this.sinkComp = sinkComp;
    }

    public void process() {
        this.sinkComp.setInput(sink, this.sourceComp.getOutput(source));
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public int getSource() {
        return source;
    }

    public int getSink() {
        return sink;
    }

    public Component getSourceComp() {
        return sourceComp;
    }

    public Component getSinkComp() {
        return sinkComp;
    }
}
