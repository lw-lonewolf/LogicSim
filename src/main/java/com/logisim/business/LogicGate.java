package com.logisim.business;

public class LogicGate {

    private int x, y; // position on canvas
    private String type;

    public LogicGate(int X, int Y, String type) {
        this.x = X;
        this.y = Y;
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
