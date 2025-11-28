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
        Bounds viewportBounds = canvasScrollPane.getViewportBounds();
        double viewWidth = viewportBounds.getWidth();
        double viewHeight = viewportBounds.getHeight();

        double scrollX =
            canvasScrollPane.getHvalue() * (canvasPane.getWidth() - viewWidth);
        double scrollY =
            canvasScrollPane.getVvalue() *
            (canvasPane.getHeight() - viewHeight);

        double centerX = scrollX + (viewWidth / 2);
        double centerY = scrollY + (viewHeight / 2);

        double snapX = Math.round((centerX - 50) / gridSize) * gridSize;
        double snapY = Math.round((centerY - 50) / gridSize) * gridSize;

        if (snapX < 0) snapX = 0;
        if (snapY < 0) snapY = 0;

        return new Point2D(snapX, snapY);
    }
}
