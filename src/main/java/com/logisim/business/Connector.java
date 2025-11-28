package com.logisim.business;

public class Connector {

    protected String name;
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
