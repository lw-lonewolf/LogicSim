package com.logisim.ui;

import com.logisim.business.LogicGate;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class ComponentPalette extends JPanel {

    private CircuitCanvas canvas;

    public ComponentPalette(CircuitCanvas canvas) {
        this.canvas = canvas;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.DARK_GRAY);

        addButton("AND Gate", 50, 50);
        addButton("OR Gate", 50, 150);
        addButton("NOT Gate", 50, 250);
    }

    private void addButton(String name, int x, int y) {
        JButton button = new JButton(name);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Add gate at fixed position for now
                    LogicGate gate = new LogicGate(x, y, name);
                    List<LogicGate> gates = new ArrayList<>();
                    gates.add(gate);
                    canvas.setGates(gates);
                    canvas.repaint();
                }
            }
        );
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(button);
    }
}
