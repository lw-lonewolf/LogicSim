package com.logisim.ui.controllers;

import com.logisim.data.CircuitDAO;
import com.logisim.domain.Circuit;
import com.logisim.domain.Project;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ProjectDashboardController {

    @FXML
    private Label lblProjectName;

    @FXML
    private ListView<Circuit> circuitList;

    @FXML
    private Button btnOpen;

    private Project currentProject;
    private final CircuitDAO circuitDao = new CircuitDAO();

    @FXML
    public void initialize() {
        circuitList
            .getSelectionModel()
            .selectedItemProperty()
            .addListener((obs, oldVal, newVal) ->
                btnOpen.setDisable(newVal == null)
            );
    }

    public void setProject(Project project) {
        this.currentProject = project;
        lblProjectName.setText(project.getName());
        refreshList();
    }

    private void refreshList() {
        circuitList.getItems().clear();
        if (currentProject != null) {
            List<Circuit> circuits = circuitDao.getCircuitsByProjectId(
                currentProject.getId()
            );
            circuitList.getItems().addAll(circuits);
        }
    }

    @FXML
    private void handleNewCircuit() {
        TextInputDialog dialog = new TextInputDialog("Main");
        dialog.setTitle("New Circuit");
        dialog.setHeaderText("Create a New Circuit");
        dialog.setContentText("Circuit Name:");

        dialog
            .getDialogPane()
            .getStylesheets()
            .add(
                getClass()
                    .getResource("/com/logisim/ui/styles/application.css")
                    .toExternalForm()
            );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            circuitDao.createCircuit(
                currentProject.getId(),
                result.get().trim()
            );
            refreshList();
        }
    }

    @FXML
    private void handleOpenCircuit() {
        Circuit selected = circuitList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openMainEditor(selected);
        }
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource("/com/logisim/ui/views/start_view.fxml")
            );
            Stage stage = (Stage) lblProjectName.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage
                .getScene()
                .getStylesheets()
                .add(
                    getClass()
                        .getResource("/com/logisim/ui/styles/application.css")
                        .toExternalForm()
                );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openMainEditor(Circuit circuit) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/logisim/ui/views/MainView.fxml")
            );
            Parent root = loader.load();

            MainViewController mainController = loader.getController();
            mainController.setContext(currentProject, circuit);

            Stage stage = (Stage) lblProjectName.getScene().getWindow();
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
}
