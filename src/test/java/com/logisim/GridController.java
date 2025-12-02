package com.logisim;

import static org.junit.jupiter.api.Assertions.*;

import com.logisim.ui.controllers.GridController;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GridControllerTest {

    @BeforeAll
    static void initJfx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {}
    }

    @Test
    void testSnapValues() {
        Canvas c = new Canvas();
        GridController gc = new GridController(c, 20);

        assertEquals(20.0, gc.snap(18.0));
        assertEquals(20.0, gc.snap(22.0));
        assertEquals(0.0, gc.snap(5.0));
        assertEquals(40.0, gc.snap(44.0));
    }

    @Test
    void testGridSize() {
        Canvas c = new Canvas();
        GridController gc = new GridController(c, 50);
        assertEquals(50, gc.getGridSize());
    }

    @Test
    void testDrawGrid() {
        Canvas c = new Canvas(100, 100);
        GridController gc = new GridController(c, 20);
        assertDoesNotThrow(gc::drawGrid);
    }
}
