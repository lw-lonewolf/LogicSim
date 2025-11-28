package com.logisim.ui.controllers;

import javafx.scene.canvas.Canvas;

public class GridController {

    private final Canvas gridCanvas;
    private final int gridSize;

    public GridController(Canvas gridCanvas, int gridSize) {
        this.gridCanvas = gridCanvas;
        this.gridSize = gridSize;

        gridCanvas
            .widthProperty()
            .addListener((obs, oldVal, newVal) -> drawGrid());
        gridCanvas
            .heightProperty()
            .addListener((obs, oldVal, newVal) -> drawGrid());
        drawGrid();
    }

    public void drawGrid() {
        var gc = gridCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());
        gc.setLineWidth(0.5);
        gc.setStroke(javafx.scene.paint.Color.LIGHTGRAY);

        for (double x = 0; x < gridCanvas.getWidth(); x += gridSize) {
            gc.strokeLine(x, 0, x, gridCanvas.getHeight());
        }

        for (double y = 0; y < gridCanvas.getHeight(); y += gridSize) {
            gc.strokeLine(0, y, gridCanvas.getWidth(), y);
        }
    }

    public double snap(double value) {
        return Math.round(value / gridSize) * gridSize;
    }

    public int getGridSize() {
        return gridSize;
    }
}
