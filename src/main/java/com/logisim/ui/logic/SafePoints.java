package com.logisim.ui.logic;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

public class SafePoints {

    public static final Point2D getSafeSpawnPoint(
        ScrollPane canvasScrollPane,
        Pane canvasPane,
        int gridSize
    ) {
        // 1. Get the visible dimensions of the viewport
        Bounds viewportBounds = canvasScrollPane.getViewportBounds();
        double viewWidth = viewportBounds.getWidth();
        double viewHeight = viewportBounds.getHeight();

        // 2. Calculate the "Scrolled" amount (Top-Left of the visible area)
        // Formula: ScrollRatio * (TotalContentSize - VisibleSize)
        double scrollX =
            canvasScrollPane.getHvalue() * (canvasPane.getWidth() - viewWidth);
        double scrollY =
            canvasScrollPane.getVvalue() *
            (canvasPane.getHeight() - viewHeight);

        // 3. Find the Center
        double centerX = scrollX + (viewWidth / 2);
        double centerY = scrollY + (viewHeight / 2);

        // 4. Snap to Grid (Optional, but keeps things clean)
        // We offset by -50 (half gate size) so the CENTER of the gate hits the screen center
        double snapX = Math.round((centerX - 50) / gridSize) * gridSize;
        double snapY = Math.round((centerY - 50) / gridSize) * gridSize;

        // Safety check: ensure we don't spawn off-screen (negative coords)
        if (snapX < 0) snapX = 0;
        if (snapY < 0) snapY = 0;

        return new Point2D(snapX, snapY);
    }
}
