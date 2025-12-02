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

/**
 * Controller class for the application's start screen.
 * <p>
 * This class handles the initial user interactions when the application launches.
 * It provides functionality to create new projects, load existing projects from the database,
 * or delete existing projects. It manages the transition from the start screen to the
 * main project dashboard.
 * </p>
 */
public class StartScreenController {

    @FXML
    private Button btnNewProject;

    @FXML
    private Button btnLoadProject;

    /**
     * Initializes the controller class.
     * <p>
     * This method is automatically called after the FXML file has been loaded.
     * It assigns specific CSS style classes to the primary buttons to ensure
     * consistent UI theming.
     * </p>
     */
    @FXML
    public void initialize() {
        btnNewProject.getStyleClass().addAll("button", "button-primary");
        btnLoadProject.getStyleClass().addAll("button", "button-secondary");
    }

    /**
     * Handles the "New Project" button action.
     * <p>
     * Opens a dialog prompting the user for a project name. If a valid name is provided,
     * a new {@link Project} is created, saved to the database, and the application
     * transitions to the dashboard view for the new project.
     * </p>
     */
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

            loadDashboard(newProject);
        }
    }

    /**
     * Handles the "Load Project" button action.
     * <p>
     * Retrieves a list of existing projects from the database using {@link ProjectDAO}.
     * If projects exist, a choice dialog is displayed. Upon selection, the application
     * transitions to the dashboard view for the selected project. If no projects exist,
     * an alert is shown.
     * </p>
     */
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
            loadDashboard(selectedProject);
        }
    }

    /**
     * Handles the "Delete Project" action.
     * <p>
     * Retrieves existing projects and prompts the user to select one for deletion.
     * If confirmed, the project (and its associated data) is removed from the database
     * via {@link ProjectDAO#deleteProject(long)}.
     * </p>
     */
    @FXML
    private void handleDeleteProject() {
        ProjectDAO dao = new ProjectDAO();
        List<Project> projects = dao.getAllProjects();

        if (projects.isEmpty()) {
            showAlert("Info", "No projects to delete.");
            return;
        }

        ChoiceDialog<Project> dialog = new ChoiceDialog<>(
            projects.get(0),
            projects
        );
        dialog.setTitle("Delete Project");
        dialog.setHeaderText(
            "WARNING: This will delete the project and ALL its circuits!"
        );
        dialog.setContentText("Select Project to Delete:");

        dialog
            .getDialogPane()
            .getStylesheets()
            .add(
                getClass()
                    .getResource("/com/logisim/ui/styles/application.css")
                    .toExternalForm()
            );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        Optional<Project> result = dialog.showAndWait();

        if (result.isPresent()) {
            dao.deleteProject(result.get().getId());
            showAlert("Success", "Project deleted successfully.");
        }
    }

    /**
     * Helper method to display a styled information alert.
     *
     * @param title   The title of the alert window.
     * @param content The message content to be displayed.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
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

    /**
     * Transitions the scene to the Project Dashboard.
     * <p>
     * Loads the {@code project_dashboard.fxml}, initializes the {@link ProjectDashboardController}
     * with the selected project context, applies the application stylesheet, and updates the stage.
     * </p>
     *
     * @param project The {@link Project} context to pass to the dashboard.
     */
    private void loadDashboard(Project project) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                    "/com/logisim/ui/views/project_dashboard.fxml"
                )
            );
            Parent root = loader.load();

            ProjectDashboardController controller = loader.getController();
            controller.setProject(project);

            Stage stage = (Stage) btnNewProject.getScene().getWindow();
            Scene scene = new Scene(root);

            scene
                .getStylesheets()
                .add(
                    getClass()
                        .getResource("/com/logisim/ui/styles/application.css")
                        .toExternalForm()
                );

            stage.setScene(scene);
            stage.setTitle("Logisim Clone - " + project.getName());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Applies the application CSS styles to a dialog pane.
     * <p>
     * This ensures that pop-up dialogs match the overall theme of the application.
     * </p>
     *
     * @param dialogPane The {@link javafx.scene.control.DialogPane} to style.
     */
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
