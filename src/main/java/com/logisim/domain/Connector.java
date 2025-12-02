package com.logisim.domain;

import com.logisim.domain.components.Component;

/**
 * Represents a wire connection between two components in the circuit.
 * <p>
 * A Connector facilitates the transmission of a logic signal (boolean state) from
 * a specific output pin of a source component to a specific input pin of a sink component.
 * </p>
 */
public class Connector {

    protected String name;
    protected int source;
    protected int sink;
    protected Component sourceComp;
    protected Component sinkComp;

    /**
     * Constructs a new Connector between two components.
     *
     * @param sourceComp The component generating the signal (source).
     * @param sinkComp   The component receiving the signal (sink).
     * @param source     The index of the output pin on the source component.
     * @param sink       The index of the input pin on the sink component.
     */
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

    /**
     * Transmits the signal from the source to the sink.
     * <p>
     * This method retrieves the current boolean value from the source component's
     * output pin (specified by {@code source}) and applies it to the sink component's
     * input pin (specified by {@code sink}).
     * </p>
     */
    public void process() {
        this.sinkComp.setInput(sink, this.sourceComp.getOutput(source));
    }

    /**
     * Retrieves the name of the connector.
     *
     * @return The name string (default "Connector").
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the index of the output pin on the source component.
     *
     * @return The integer index of the source pin.
     */
    public int getSource() {
        return source;
    }

    /**
     * Retrieves the index of the input pin on the sink component.
     *
     * @return The integer index of the sink pin.
     */
    public int getSink() {
        return sink;
    }

    /**
     * Retrieves the source component associated with this connection.
     *
     * @return The {@link Component} instance acting as the source.
     */
    public Component getSourceComp() {
        return sourceComp;
    }

    /**
     * Retrieves the sink component associated with this connection.
     *
     * @return The {@link Component} instance acting as the sink.
     */
    public Component getSinkComp() {
        return sinkComp;
    }
}
