package com.logisim.ui.controllers;

import com.logisim.business.Project;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class StartScreenController {

    @FXML
    private void handleNewProject() {
        TextInputDialog dialog = new TextInputDialog("New Circuit");
        dialog.setTitle("New Project");
        dialog.setHeaderText("Create a New Project");
        dialog.setContentText("Project Name:");

        dialog.getDialogPane().setStyle("-fx-background-color: #2b2b2b;");
        dialog.getDialogPane().getStyleClass().add("dark-dialog");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String name = result.get();

            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Project Folder");
            File selectedDirectory = directoryChooser.showDialog(null);

            if (selectedDirectory != null) {
                Project newProject = new Project(
                    name,
                    selectedDirectory.getAbsolutePath()
                );

                loadMainEditor(newProject);
            }
        }
    }

    @FXML
    private void handleLoadProject() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Project File");

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Project loadedProject = new Project(
                "Loaded Project",
                file.getParent()
            );
            loadedProject.load();

            loadMainEditor(loadedProject);
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
            stage.setTitle("Logisim - " + project.getName());
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load editor");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private javafx.scene.control.Button btnNewProject;
}
