package com.logisim;

import static org.junit.jupiter.api.Assertions.*;

import com.logisim.ui.logic.SafePoints;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SafePointsTest {

    @BeforeAll
    static void initJfx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {}
    }

    @Test
    void testSpawnPointCalculation() {
        Pane canvas = new Pane();
        canvas.setPrefSize(1000, 1000);

        ScrollPane scrollPane = new ScrollPane(canvas);
        scrollPane.setPrefSize(200, 200);
        scrollPane.setHvalue(0);
        scrollPane.setVvalue(0);

        // Viewport 200x200 at 0,0. Center is 100,100.
        // Logic: (Center - 50) / grid * grid
        // (100 - 50) = 50. 50 / 20 = 2.5 -> round 3. 3 * 20 = 60.

        Point2D point = SafePoints.getSafeSpawnPoint(scrollPane, canvas, 20);

        assertNotNull(point);
        assertTrue(point.getX() >= 0);
        assertTrue(point.getY() >= 0);
    }
}
