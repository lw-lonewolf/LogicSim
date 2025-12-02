package com.logisim.ui.logic;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

/**
 * Utility class providing logic for determining safe spawn locations for UI components.
 * <p>
 * This class helps prevent usability issues where new components might appear off-screen
 * or in obscured areas by calculating coordinates relative to the user's current viewport.
 * </p>
 */
public class SafePoints {

    /**
     * Calculates a spawn point in the center of the currently visible viewport area.
     * <p>
     * This method calculates the center coordinates of the {@code canvasScrollPane}'s viewport,
     * taking into account the current scroll position. It then adjusts these coordinates
     * to snap to the nearest grid lines defined by {@code gridSize}. This ensures that
     * when a user adds a component, it appears right in front of them, even if they have
     * scrolled away from the origin (0,0).
     * </p>
     *
     * @param canvasScrollPane The scroll pane containing the canvas. Used to determine visible area and scroll offsets.
     * @param canvasPane       The actual content pane representing the circuit canvas. Used to determine total dimensions.
     * @param gridSize         The size of the grid cells for snapping calculations.
     * @return A {@link Point2D} representing the calculated X and Y coordinates for spawning a component.
     */
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
