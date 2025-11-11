package com.logisim.ui;

import com.logisim.business.LogicGate;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class CircuitCanvas extends JPanel {

    private List<LogicGate> gates;

    public CircuitCanvas() {
        this.gates = new ArrayList<>();
        setBackground(Color.DARK_GRAY);
    }

    public void setGates(List<LogicGate> gates) {
        this.gates = gates;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.GRAY);
        for (int x = 0; x < getWidth(); x += 20) {
            g.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y < getHeight(); y += 20) {
            g.drawLine(0, y, getWidth(), y);
        }

        for (LogicGate gate : gates) {
            drawGate(g, gate);
        }
    }

    private void drawGate(Graphics g, LogicGate gate) {
        int x = gate.getX();
        int y = gate.getY();
        g.setColor(Color.ORANGE);
        g.fillRect(x, y, 60, 40);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, 60, 40);
        g.drawString(gate.getType(), x + 15, y + 25);
    }
}
