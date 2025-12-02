package com.logisim.ui.controllers;

import com.logisim.data.CircuitDAO;
import com.logisim.data.DatabaseManager;
import com.logisim.domain.Circuit;
import com.logisim.domain.Project;
import com.logisim.domain.components.And;
import com.logisim.domain.components.Bulb;
import com.logisim.domain.components.Component;
import com.logisim.domain.components.Not;
import com.logisim.domain.components.Or;
import com.logisim.domain.components.SubCircuitComponent;
import com.logisim.domain.components.Switch;
import com.logisim.ui.components.GateFactory;
import com.logisim.ui.components.Port;
import com.logisim.ui.components.Wire;
import com.logisim.ui.logic.ConnectionManager;
import com.logisim.ui.logic.SafePoints;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
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

    @FXML
    private VBox subCircuitContainer;

    private static final int gridSize = 20;

    private GridController gridController;
    private Project currentProject;
    private Circuit currentCircuit;
    private CircuitDAO circuitDAO = new CircuitDAO();

    private void refreshSubCircuitSidebar() {
        if (subCircuitContainer == null) {
            System.err.println(
                "CRITICAL ERROR: subCircuitContainer is NULL. Check fx:id in FXML."
            );
            return;
        }

        subCircuitContainer.getChildren().clear();

        if (currentProject == null || currentCircuit == null) {
            System.out.println(
                "DEBUG: Cannot load subcircuits (Project/Circuit context is null)"
            );
            return;
        }

        List<Circuit> availableCircuits = circuitDAO.getCircuitsByProjectId(
            currentProject.getId()
        );

        System.out.println(
            "DEBUG: Found " +
                availableCircuits.size() +
                " total circuits in project."
        );
        int count = 0;
        for (Circuit template : availableCircuits) {
            if (template.getId() == currentCircuit.getId()) {
                System.out.println(
                    "DEBUG: Skipping '" +
                        template.getName() +
                        "' (Cannot import self)"
                );
                continue;
            }

            Button btn = new Button(template.getName());
            btn.setMaxWidth(Double.MAX_VALUE);

            btn.getStyleClass().add("button");
            btn.setStyle("-fx-border-color: #555;");

            btn.setOnAction(e -> spawnSubCircuit(template));

            subCircuitContainer.getChildren().add(btn);
            count++;
        }
        System.out.println(
            "DEBUG: Added " + count + " subcircuit buttons to sidebar."
        );
    }

    private void spawnSubCircuit(Circuit template) {
        Circuit innerLogic = loadFullCircuitFromDB(template.getId());
        innerLogic.setName(template.getName());

        Point2D pos = SafePoints.getSafeSpawnPoint(
            canvasScrollPane,
            canvasPane,
            gridSize
        );

        SubCircuitComponent subComp = new SubCircuitComponent(innerLogic);

        subComp.setPositionX(pos.getX());
        subComp.setPositionY(pos.getY());

        currentCircuit.addComponent(subComp);

        StackPane visual = GateFactory.createGateWithHitBox(
            "subcircuit",
            pos.getX(),
            pos.getY(),
            canvasPane,
            gridController,
            (Component) subComp,
            this::handleDeleteGate,
            this::handleToggleSwitch
        );

        canvasPane.getChildren().add(visual);
        System.out.println("Spawned SubCircuit: " + template.getName());
    }

    @FXML
    private void handleBackToDashboard() {
        // 1. Optional: Auto-save check or confirmation could go here

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                    "/com/logisim/ui/views/project_dashboard.fxml"
                )
            );
            Parent root = loader.load();

            ProjectDashboardController controller = loader.getController();
            controller.setProject(this.currentProject);

            Stage stage = (Stage) canvasPane.getScene().getWindow();
            Scene scene = new Scene(root);

            scene
                .getStylesheets()
                .add(
                    getClass()
                        .getResource("/com/logisim/ui/styles/application.css")
                        .toExternalForm()
                );

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Circuit loadFullCircuitFromDB(long id) {
        Circuit c = new Circuit();
        c.setId(id);
        String sql = "SELECT name FROM circuits WHERE id = ?";
        try (
            Connection conn = DatabaseManager.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    c.setName(rs.getString("name"));
                } else {
                    c.setName("SubCircuit");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            c.setName("Error");
        }
        c.setName("SubInstance");

        List<Component> comps = circuitDAO.loadComponents(id);

        List<CircuitDAO.ConnectionRecord> rawConns = circuitDAO.loadConnections(
            id
        );

        Map<String, Component> map = new HashMap<>();
        for (Component comp : comps) {
            map.put(comp.getUuid(), comp);
            c.addComponent(comp);
        }

        for (CircuitDAO.ConnectionRecord r : rawConns) {
            Component src = map.get(r.sourceUuid());
            Component sink = map.get(r.sinkUuid());
            if (src != null && sink != null) {
                c.addConnection(r.sourcePin(), src, r.sinkPin(), sink);
            }
        }
        return c;
    }

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
    public void handleAnalyze() {
        if (currentCircuit == null) return;

        List<Component> inputs = currentCircuit
            .getComponents()
            .stream()
            .filter(c -> c instanceof Switch)
            .collect(Collectors.toList());

        List<Component> outputs = currentCircuit
            .getComponents()
            .stream()
            .filter(c -> c instanceof Bulb)
            .collect(Collectors.toList());

        int inputCount = inputs.size();
        int outputCount = outputs.size();

        if (inputCount == 0) {
            showAlert("Error", "Analysis Failed, No inputs exist.");
            return;
        }

        if (outputCount == 0) {
            showAlert("Error", "No Outputs (Bulbs) found.");
            return;
        }

        List<String> columnHeaders = new ArrayList<>();
        for (int i = 0; i < inputCount; i++) {
            columnHeaders.add(String.valueOf((char) ('A' + i)));
        }

        for (int i = 0; i < outputCount; i++) {
            columnHeaders.add(String.valueOf("Y" + (i + 1)));
        }

        try {
            boolean[][] truthTable = currentCircuit.analyze();
            if (truthTable.length == 0) {
                showAlert(
                    "Analysis Failed",
                    "Circuit logic could not be simulated."
                );
                return;
            }
            String expression = currentCircuit.generateBooleanExpression(
                truthTable,
                columnHeaders.subList(0, inputCount)
            );
            showAnalysisWindow(truthTable, columnHeaders, expression);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Analysis Error: " + e.getMessage());
        }
    }

    private void showAnalysisWindow(
        boolean[][] rawData,
        List<String> headers,
        String expression
    ) {
        Stage stage = new Stage();
        stage.setTitle("Circuit Analysis");

        VBox layout = new VBox(15);
        layout.setStyle("-fx-background-color: #1e1e1e; -fx-padding: 20;");

        Label lblExpr = new Label("Boolean Expression:");
        lblExpr.getStyleClass().add("header-label");

        TextArea txtExpression = new TextArea(expression);
        txtExpression.setEditable(false);
        txtExpression.setWrapText(true);
        txtExpression.setPrefRowCount(2);
        txtExpression.setStyle(
            "-fx-font-family: 'Consolas', monospace; -fx-font-size: 14px;"
        );

        Label lblTable = new Label("Truth Table:");
        lblTable.getStyleClass().add("header-label");

        TableView<ObservableList<String>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        for (int i = 0; i < headers.size(); i++) {
            final int colIndex = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(
                headers.get(i)
            );

            col.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().get(colIndex))
            );

            col.setStyle("-fx-alignment: CENTER;");
            table.getColumns().add(col);
        }

        ObservableList<ObservableList<String>> data =
            FXCollections.observableArrayList();
        for (boolean[] row : rawData) {
            ObservableList<String> rowList =
                FXCollections.observableArrayList();
            for (boolean cell : row) {
                rowList.add(cell ? "1" : "0");
            }
            data.add(rowList);
        }
        table.setItems(data);

        layout.getChildren().addAll(lblExpr, txtExpression, lblTable, table);

        Scene scene = new Scene(layout, 500, 600);

        scene
            .getStylesheets()
            .add(
                getClass()
                    .getResource("/com/logisim/ui/styles/application.css")
                    .toExternalForm()
            );

        stage.setScene(scene);
        stage.initOwner(btnAnd.getScene().getWindow());
        stage.show();
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

        canvasPane.setOnMouseMoved(e -> {
            connectionManager.onMouseMove(e);
        });

        canvasPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                connectionManager.cancelConnection();
            }
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
        refreshSubCircuitSidebar();
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
