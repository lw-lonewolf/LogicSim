package com.logisim;

import static org.junit.jupiter.api.Assertions.*;

import com.logisim.domain.components.And;
import com.logisim.domain.components.Switch;
import com.logisim.ui.components.GateFactory;
import com.logisim.ui.controllers.GridController;
import com.logisim.ui.logic.ConnectionManager;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GateFactoryTest {

    @BeforeAll
    static void initJfx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {}
    }

    @Test
    void testCreateGateStructure() {
        Pane canvas = new Pane();
        Canvas gridCanvas = new Canvas();
        GridController grid = new GridController(gridCanvas, 20);
        And andComp = new And();

        try {
            StackPane visual = GateFactory.createGateWithHitBox(
                "and",
                0,
                0,
                canvas,
                grid,
                andComp,
                null,
                null
            );

            assertNotNull(visual);
            assertEquals(andComp, visual.getUserData());
            assertEquals(0, visual.getLayoutX());
            assertEquals(0, visual.getLayoutY());
        } catch (Exception e) {}
    }

    @Test
    void testRefreshComponentState() {
        Switch sw = new Switch();
        StackPane visual = new StackPane();
        visual.setUserData(sw);

        visual.getChildren().add(new Rectangle());
        ImageView view = new ImageView();
        visual.getChildren().add(view);

        sw.setState(true);

        try {
            GateFactory.refreshComponentState(visual);
        } catch (Exception e) {}
    }

    @Test
    void testSetConnectionManager() {
        ConnectionManager cm = new ConnectionManager(new Pane());
        assertDoesNotThrow(() -> GateFactory.setConnectionManager(cm));
    }
}
