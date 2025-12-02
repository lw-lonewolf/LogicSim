package com.logisim.ui.controllers;

import javafx.scene.canvas.Canvas;

/**
 * Controller responsible for managing the background grid of the circuit editor.
 * <p>
 * This class handles the rendering of the visual grid lines on a JavaFX {@link Canvas}.
 * It also provides utility methods for "snapping" coordinates to the nearest grid points,
 * ensuring that components align neatly within the workspace.
 * </p>
 */
public class GridController {

    /**
     * The canvas element where the grid lines are drawn.
     */
    private final Canvas gridCanvas;

    /**
     * The spacing between grid lines in pixels (cell size).
     */
    private final int gridSize;

    /**
     * Constructs a new GridController.
     * <p>
     * Initializes the controller with a target canvas and specific grid size.
     * It also sets up listeners on the canvas's width and height properties to
     * automatically redraw the grid whenever the window is resized.
     * </p>
     *
     * @param gridCanvas The JavaFX {@link Canvas} to draw the grid upon.
     * @param gridSize   The distance in pixels between grid lines.
     */
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

    /**
     * Renders the grid lines onto the canvas.
     * <p>
     * This method first clears the entire canvas and then draws vertical and horizontal
     * lines spaced by {@code gridSize}. The lines are drawn with a light gray color
     * to serve as a subtle visual guide.
     * </p>
     */
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

    /**
     * Aligns a raw coordinate value to the nearest grid line.
     * <p>
     * This method rounds the provided value to the nearest multiple of {@code gridSize}.
     * It is used to ensure components snap to the grid when dragged or placed.
     * </p>
     *
     * @param value The raw coordinate value (X or Y).
     * @return The coordinate value rounded to the nearest grid interval.
     */
    public double snap(double value) {
        return Math.round(value / gridSize) * gridSize;
    }

    /**
     * Retrieves the configured grid size.
     *
     * @return The size of the grid cells in pixels.
     */
    public int getGridSize() {
        return gridSize;
    }
}
