package com.logisim.ui.controllers;

import com.logisim.data.ProjectDAO;
import com.logisim.domain.Project;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class StartScreenController {

    @FXML
    private Button btnNewProject;

    @FXML
    private Button btnLoadProject;

    @FXML
    public void initialize() {
        btnNewProject.getStyleClass().addAll("button", "button-primary");
        btnLoadProject.getStyleClass().addAll("button", "button-secondary");
    }

    @FXML
    private void handleNewProject() {
        TextInputDialog dialog = new TextInputDialog("New Circuit");
        dialog.setTitle("New Project");
        dialog.setHeaderText("Create a New Project");
        dialog.setContentText("Enter Project Name:");

        styleDialog(dialog.getDialogPane());

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String name = result.get().trim();

            Project newProject = new Project(name);
            newProject.save();

            loadMainEditor(newProject);
        }
    }

    @FXML
    private void handleLoadProject() {
        ProjectDAO dao = new ProjectDAO();
        List<Project> projects = dao.getAllProjects();

        if (projects.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Projects");
            alert.setHeaderText(null);
            alert.setContentText(
                "No projects found in the database. Create one first!"
            );
            styleDialog(alert.getDialogPane());
            alert.showAndWait();
            return;
        }

        ChoiceDialog<Project> dialog = new ChoiceDialog<>(
            projects.get(0),
            projects
        );
        dialog.setTitle("Open Project");
        dialog.setHeaderText("Select a Project to Load");
        dialog.setContentText("Project:");

        styleDialog(dialog.getDialogPane());

        Optional<Project> result = dialog.showAndWait();

        if (result.isPresent()) {
            Project selectedProject = result.get();
            // TODO: load Circuits/Components for this project here
            loadMainEditor(selectedProject);
        }
    }

    private void loadMainEditor(Project project) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/logisim/ui/views/MainView.fxml")
            );
            Parent root = loader.load();

            MainViewController mainController = loader.getController();
            mainController.setCurrentProject(project);

            Stage stage = (Stage) btnNewProject.getScene().getWindow();
            Scene scene = new Scene(root);
            scene
                .getStylesheets()
                .add(
                    getClass()
                        .getResource("/com/logisim/ui/styles/canvasPane.css")
                        .toExternalForm()
                );

            stage.setScene(scene);
            stage.setTitle("Logisim Clone - " + project.getName());
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void styleDialog(javafx.scene.control.DialogPane dialogPane) {
        dialogPane
            .getStylesheets()
            .add(
                getClass()
                    .getResource("/com/logisim/ui/styles/application.css")
                    .toExternalForm()
            );
        dialogPane.getStyleClass().add("dialog-pane");
    }
}
