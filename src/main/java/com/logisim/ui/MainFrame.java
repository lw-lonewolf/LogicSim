package com.logisim.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.logisim.ui.ComponentPalette;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

public class MainFrame extends JFrame {

    private CircuitCanvas canvas;
    private ComponentPalette palette;

    public MainFrame() {
        // Set FlatLaf theme
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("LogiSim - Circuit Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        // Use MigLayout
        setLayout(new MigLayout("fill", "200[]10[]", "[]"));

        // Initialize canvas and palette
        canvas = new CircuitCanvas();
        palette = new ComponentPalette(canvas);

        // Add to frame: palette on left, canvas on right
        add(palette, "w 200!, h 100%!, gapright 10");
        add(canvas, "w 100%, h 100%");

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
