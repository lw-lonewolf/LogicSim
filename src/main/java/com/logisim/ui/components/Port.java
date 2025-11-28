package com.logisim.ui.components;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Port extends Circle {

    private boolean isInput;
    private boolean selected;
    private int index;
    private StackPane parentGate;

    public Port(boolean isInput, StackPane parentGate, int index) {
        super(5);
        this.isInput = isInput;
        this.selected = false;
        this.parentGate = parentGate;
        this.index = index;
        setFill(Color.TRANSPARENT);
        setStroke(Color.BLUE);
        setStrokeWidth(2);

        setOnMouseEntered(e -> setFill(Color.YELLOW));
        setOnMouseExited(e -> setFill(Color.TRANSPARENT));
    }

    public boolean isInput() {
        return isInput;
    }

    public void setSelected(boolean value) {
        selected = value;
    }

    public StackPane getParentGate() {
        return parentGate;
    }

    public boolean isSelected() {
        return selected;
    }

    public int getIndex() {
        return index;
    }
}
