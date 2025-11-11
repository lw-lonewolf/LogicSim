package com.logisim.controller;

import com.logisim.business.LogicGate;
import com.logisim.ui.CircuitCanvas;
import java.util.ArrayList;
import java.util.List;

public class CircuitController {

    private CircuitCanvas canvas;
    private List<LogicGate> gates;

    public CircuitController(CircuitCanvas canvas) {
        this.canvas = canvas;
        this.gates = new ArrayList<>();
    }

    public void addGate(int x, int y, String type) {
        LogicGate gate = new LogicGate(x, y, type);
        gates.add(gate);
        canvas.setGates(gates);
        canvas.repaint();
    }
}
