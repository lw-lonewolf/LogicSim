package com.logisim.ui.controllers;

import com.logisim.business.And;
import com.logisim.business.Component;
import com.logisim.business.Not;
import com.logisim.business.Or;
import com.logisim.business.Project;
import com.logisim.helper.SafePoints;
import com.logisim.ui.model.ConnectionManager;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainViewController {

    @FXML
    private VBox sidebar;

    @FXML
    private Pane canvasPane;

    @FXML
    private ScrollPane canvasScrollPane;

    @FXML
    private Button btnAnd, btnOr, btnNot;

    @FXML
    private Canvas gridCanvas;

    private static final int gridSize = 20;

    private GridController gridController;
    private Project currentProject;

    @FXML
    public void initialize() {
        gridController = new GridController(gridCanvas, gridSize);

        ConnectionManager connectionManager = new ConnectionManager(canvasPane);
        GateController.setConnectionManager(connectionManager);

        btnAnd.setOnAction(e -> {
            Point2D pos = SafePoints.getSafeSpawnPoint(
                canvasScrollPane,
                canvasPane,
                gridSize
            );
            Component comp = new And();
            StackPane gate = GateController.createGateWithHitBox(
                "and",
                pos.getX(),
                pos.getY(),
                canvasPane,
                gridController,
                comp
            );
            canvasPane.getChildren().add(gate);
        });

        btnNot.setOnAction(e -> {
            Point2D pos = SafePoints.getSafeSpawnPoint(
                canvasScrollPane,
                canvasPane,
                gridSize
            );
            Component comp = new Not();
            StackPane gate = GateController.createGateWithHitBox(
                "not",
                pos.getX(),
                pos.getY(),
                canvasPane,
                gridController,
                comp
            );
            canvasPane.getChildren().add(gate);
        });

        btnOr.setOnAction(e -> {
            Point2D pos = SafePoints.getSafeSpawnPoint(
                canvasScrollPane,
                canvasPane,
                gridSize
            );
            Component comp = new Or();
            StackPane gate = GateController.createGateWithHitBox(
                "or",
                pos.getX(),
                pos.getY(),
                canvasPane,
                gridController,
                comp
            );
            gate.setUserData(comp);
            canvasPane.getChildren().add(gate);
        });
    }

    public VBox getSidebar() {
        return sidebar;
    }

    public void setSidebar(VBox sidebar) {
        this.sidebar = sidebar;
    }

    public Pane getCanvasPane() {
        return canvasPane;
    }

    public void setCanvasPane(Pane canvasPane) {
        this.canvasPane = canvasPane;
    }

    public ScrollPane getCanvasScrollPane() {
        return canvasScrollPane;
    }

    public void setCanvasScrollPane(ScrollPane canvasScrollPane) {
        this.canvasScrollPane = canvasScrollPane;
    }

    public Button getBtnAnd() {
        return btnAnd;
    }

    public void setBtnAnd(Button btnAnd) {
        this.btnAnd = btnAnd;
    }

    public Button getBtnOr() {
        return btnOr;
    }

    public void setBtnOr(Button btnOr) {
        this.btnOr = btnOr;
    }

    public Button getBtnNot() {
        return btnNot;
    }

    public void setBtnNot(Button btnNot) {
        this.btnNot = btnNot;
    }

    public Canvas getGridCanvas() {
        return gridCanvas;
    }

    public void setGridCanvas(Canvas gridCanvas) {
        this.gridCanvas = gridCanvas;
    }

    public static int getGridsize() {
        return gridSize;
    }

    public GridController getGridController() {
        return gridController;
    }

    public void setGridController(GridController gridController) {
        this.gridController = gridController;
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(Project currentProject) {
        this.currentProject = currentProject;
    }
}
