package com.logisim.ui.controllers;

import com.logisim.data.CircuitDAO;
import com.logisim.domain.Circuit;
import com.logisim.domain.Project;
import com.logisim.domain.components.And;
import com.logisim.domain.components.Bulb;
import com.logisim.domain.components.Component;
import com.logisim.domain.components.Not;
import com.logisim.domain.components.Or;
import com.logisim.domain.components.Switch;
import com.logisim.ui.components.GateFactory;
import com.logisim.ui.components.Port;
import com.logisim.ui.components.Wire;
import com.logisim.ui.logic.ConnectionManager;
import com.logisim.ui.logic.SafePoints;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainViewController {

    @FXML
    private VBox sidebar;

    @FXML
    private Pane canvasPane;

    @FXML
    private ScrollPane canvasScrollPane;

    @FXML
    private Button btnAnd, btnOr, btnNot, btnSwitch, btnBulb;

    @FXML
    private Canvas gridCanvas;

    private static final int gridSize = 20;

    private GridController gridController;
    private Project currentProject;
    private Circuit currentCircuit;
    private CircuitDAO circuitDAO = new CircuitDAO();

    @FXML
    private void handleSave() {
        if (currentCircuit == null) {
            showAlert("Error", "No circuit context found.");
            return;
        }
        System.out.println(
            "DEBUG: Saving Circuit ID: " + currentCircuit.getId()
        );
        System.out.println(
            "DEBUG: Components Count: " + currentCircuit.getComponents().size()
        );
        System.out.println(
            "DEBUG: Connectors Count: " + currentCircuit.getConnectors().size()
        );

        if (currentCircuit.getComponents().isEmpty()) {
            System.err.println(
                "WARNING: You are saving an empty circuit! (Visuals might exist, but Data list is empty)"
            );
        }

        try {
            circuitDAO.updateCircuit(currentCircuit);
            showAlert("Success", "Circuit saved successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to Save Circuit: " + e.getMessage());
        }
    }

    @FXML
    private void handleRun() {
        if (currentCircuit == null) return;
        System.out.println("Running Sim");
        for (int i = 0; i < currentCircuit.getComponents().size(); i++) {
            currentCircuit.simulate();
        }

        for (Node node : canvasPane.getChildren()) {
            if (node instanceof StackPane) {
                StackPane visualGate = (StackPane) node;
                if (visualGate.getUserData() instanceof Component) {
                    GateFactory.refreshComponentState(visualGate);
                }
            }
        }
        System.out.println("Simulation Complete.");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Stage stage = (Stage) canvasPane.getScene().getWindow();
        alert.initOwner(stage);

        alert
            .getDialogPane()
            .getStylesheets()
            .add(
                getClass()
                    .getResource("/com/logisim/ui/styles/application.css")
                    .toExternalForm()
            );
        alert.getDialogPane().getStyleClass().add("dialog-pane");

        alert.showAndWait();
    }

    @FXML
    public void initialize() {
        gridCanvas.widthProperty().bind(canvasPane.widthProperty());
        gridCanvas.heightProperty().bind(canvasPane.heightProperty());

        gridController = new GridController(gridCanvas, gridSize);
        ConnectionManager connectionManager = new ConnectionManager(canvasPane);
        GateFactory.setConnectionManager(connectionManager);
        connectionManager.setOnConnectionAdded(connector -> {
            if (currentCircuit != null) {
                currentCircuit.addConnection(
                    connector.getSource(),
                    connector.getSourceComp(),
                    connector.getSink(),
                    connector.getSinkComp()
                );
                System.out.println(
                    "Logic Connection stored in Circuit ID: " +
                        currentCircuit.getId()
                );
            }
        });

        canvasPane
            .widthProperty()
            .addListener(obs -> gridController.drawGrid());
        canvasPane
            .heightProperty()
            .addListener(obs -> gridController.drawGrid());

        btnAnd.setOnAction(e -> {
            Point2D pos = SafePoints.getSafeSpawnPoint(
                canvasScrollPane,
                canvasPane,
                gridSize
            );
            Component comp = new And();
            comp.setPositionX(pos.getX());
            comp.setPositionY(pos.getY());
            currentCircuit.addComponent(comp);

            StackPane gate = GateFactory.createGateWithHitBox(
                "and",
                pos.getX(),
                pos.getY(),
                canvasPane,
                gridController,
                comp,
                this::handleDeleteGate,
                this::handleToggleSwitch
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
            comp.setPositionX(pos.getX());
            comp.setPositionY(pos.getY());
            currentCircuit.addComponent(comp);
            StackPane gate = GateFactory.createGateWithHitBox(
                "not",
                pos.getX(),
                pos.getY(),
                canvasPane,
                gridController,
                comp,
                this::handleDeleteGate,
                this::handleToggleSwitch
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
            comp.setPositionX(pos.getX());
            comp.setPositionY(pos.getY());
            currentCircuit.addComponent(comp);
            StackPane gate = GateFactory.createGateWithHitBox(
                "or",
                pos.getX(),
                pos.getY(),
                canvasPane,
                gridController,
                comp,
                this::handleDeleteGate,
                this::handleToggleSwitch
            );
            gate.setUserData(comp);
            canvasPane.getChildren().add(gate);
        });

        btnSwitch.setOnAction(e -> {
            Point2D pos = SafePoints.getSafeSpawnPoint(
                canvasScrollPane,
                canvasPane,
                gridSize
            );
            Component comp = new Switch();
            comp.setPositionX(pos.getX());
            comp.setPositionY(pos.getY());
            currentCircuit.addComponent(comp);
            StackPane gate = GateFactory.createGateWithHitBox(
                "switch",
                pos.getX(),
                pos.getY(),
                canvasPane,
                gridController,
                comp,
                this::handleDeleteGate,
                this::handleToggleSwitch
            );
            gate.setUserData(comp);
            canvasPane.getChildren().add(gate);
        });

        btnBulb.setOnAction(e -> {
            Point2D pos = SafePoints.getSafeSpawnPoint(
                canvasScrollPane,
                canvasPane,
                gridSize
            );
            Component comp = new Bulb();
            comp.setPositionX(pos.getX());
            comp.setPositionY(pos.getY());
            currentCircuit.addComponent(comp);
            StackPane gate = GateFactory.createGateWithHitBox(
                "bulb",
                pos.getX(),
                pos.getY(),
                canvasPane,
                gridController,
                comp,
                this::handleDeleteGate,
                this::handleToggleSwitch
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

    public void setContext(Project project, Circuit circuit) {
        this.currentProject = project;
        this.currentCircuit = circuit;
        System.out.println(
            "=== LOADING CIRCUIT: " +
                circuit.getName() +
                " (ID: " +
                circuit.getId() +
                ") ==="
        );
        canvasPane.getChildren().clear();
        List<Component> loadedComponents = circuitDAO.loadComponents(
            circuit.getId()
        );
        System.out.println(
            "DEBUG: Found " + loadedComponents.size() + " components in DB."
        );
        Map<String, StackPane> uuidToVisualMap = new HashMap<>();
        for (Component comp : loadedComponents) {
            circuit.addComponent(comp);
            String gateType = comp.getName();
            System.out.println(
                "DEBUG: Creating visual for " +
                    gateType +
                    " at " +
                    comp.getPositionX() +
                    "," +
                    comp.getPositionY()
            );
            StackPane visualGate = GateFactory.createGateWithHitBox(
                gateType,
                comp.getPositionX(),
                comp.getPositionY(),
                canvasPane,
                gridController,
                comp,
                this::handleDeleteGate,
                this::handleToggleSwitch
            );
            canvasPane.getChildren().add(visualGate);
            uuidToVisualMap.put(comp.getUuid(), visualGate);
        }
        List<CircuitDAO.ConnectionRecord> rawConnections =
            circuitDAO.loadConnections(circuit.getId());
        System.out.println(
            "DEBUG: Found " + rawConnections.size() + " connections in DB."
        );
        for (CircuitDAO.ConnectionRecord connData : rawConnections) {
            StackPane sourceGate = uuidToVisualMap.get(connData.sourceUuid());
            StackPane sinkGate = uuidToVisualMap.get(connData.sinkUuid());
            if (sourceGate != null && sinkGate != null) {
                Port sourcePort = findPort(
                    sourceGate,
                    false,
                    connData.sourcePin()
                );
                Port sinkPort = findPort(sinkGate, true, connData.sinkPin());

                if (sourcePort != null && sinkPort != null) {
                    Wire wire = new Wire(sourcePort, sinkPort);
                    canvasPane.getChildren().add(wire);
                    sinkPort.setConnectionState(true);
                    Component sourceComp = (Component) sourceGate.getUserData();
                    Component sinkComp = (Component) sinkGate.getUserData();
                    circuit.addConnection(
                        connData.sourcePin(),
                        sourceComp,
                        connData.sinkPin(),
                        sinkComp
                    );
                }
            }
        }
    }

    private void handleDeleteGate(StackPane visualGate) {
        Component comp = (Component) visualGate.getUserData();
        if (currentCircuit != null && comp != null) {
            currentCircuit.removeComponent(comp);
        }

        canvasPane
            .getChildren()
            .removeIf(node -> {
                if (node instanceof Wire) {
                    Wire wire = (Wire) node;
                    if (
                        wire.getSource().getParentGate() == visualGate ||
                        wire.getSink().getParentGate() == visualGate
                    ) {
                        if (wire.getSink().getParentGate() != visualGate) {
                            wire.getSink().setConnectionState(false);
                        }
                        return true;
                    }
                }
                return false;
            });
        canvasPane.getChildren().remove(visualGate);
    }

    private void handleToggleSwitch(StackPane visualGate) {
        handleRun();
    }

    /**
     * Helper to find a specific Port inside a Gate's StackPane.
     *
     * @param gate The StackPane representing the gate
     * @param isInput True if we want an input port, False for output
     * @param index The index (e.g., 0 for top input, 1 for bottom input)
     */
    private Port findPort(StackPane gate, boolean isInput, int index) {
        int currentInputIndex = 0;
        int currentOutputIndex = 0;

        for (Node child : gate.getChildren()) {
            if (child instanceof Port) {
                Port p = (Port) child;

                if (p.isInput() == isInput) {
                    if (isInput) {
                        if (currentInputIndex == index) return p;
                        currentInputIndex++;
                    } else {
                        if (currentOutputIndex == index) return p;
                        currentOutputIndex++;
                    }
                }
            }
        }
        return null;
    }
}
