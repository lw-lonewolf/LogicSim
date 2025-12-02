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

/**
 * Controller class for the Project Dashboard view.
 * <p>
 * This class manages the UI that appears after a project is selected or created.
 * It displays a list of circuits contained within the current project and provides
 * functionality to open existing circuits, create new ones, delete them, or export
 * the entire project.
 * </p>
 */
public class ProjectDashboardController {

    @FXML
    private Label lblProjectName;

    @FXML
    private ListView<Circuit> circuitList;

    @FXML
    private Button btnOpen;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnExport;

    /**
     * The currently active project being managed in this dashboard.
     */
    private Project currentProject;

    /**
     * Data Access Object used for performing database operations on circuits.
     */
    private final CircuitDAO circuitDao = new CircuitDAO();

    /**
     * Initializes the controller class.
     * <p>
     * This method is automatically called after the FXML file has been loaded.
     * It sets up a listener on the {@code circuitList} to enable or disable the
     * "Open" and "Delete" buttons based on whether a circuit is currently selected.
     * </p>
     */
    @FXML
    public void initialize() {
        circuitList
            .getSelectionModel()
            .selectedItemProperty()
            .addListener((obs, oldVal, newVal) -> {
                boolean hasSelection = (newVal != null);
                btnOpen.setDisable(newVal == null);
                if (btnDelete != null) btnDelete.setDisable(!hasSelection);
            });
    }

    /**
     * Sets the project context for this dashboard.
     * <p>
     * This method updates the UI to display the name of the provided project
     * and refreshes the list of associated circuits.
     * </p>
     *
     * @param project The {@link Project} object to display and manage.
     */
    public void setProject(Project project) {
        this.currentProject = project;
        lblProjectName.setText(project.getName());
        refreshList();
    }

    /**
     * Refreshes the ListView with the latest circuits from the database.
     * <p>
     * It queries the {@link CircuitDAO} for all circuits associated with the
     * {@code currentProject}'s ID and populates the {@code circuitList}.
     * </p>
     */
    private void refreshList() {
        circuitList.getItems().clear();
        if (currentProject != null) {
            List<Circuit> circuits = circuitDao.getCircuitsByProjectId(
                currentProject.getId()
            );
            circuitList.getItems().addAll(circuits);
        }
    }

    /**
     * Handles the "Delete Circuit" button action.
     * <p>
     * Prompts the user with a confirmation dialog. If confirmed, the selected
     * circuit is removed from the database, and the list is refreshed.
     * </p>
     */
    @FXML
    private void handleDeleteCircuit() {
        Circuit selected = circuitList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Circuit");
        alert.setHeaderText("Delete '" + selected.getName() + "'?");
        alert.setContentText("This cannot be undone.");

        styleDialog(alert.getDialogPane());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            circuitDao.deleteCircuit(selected.getId());
            refreshList();
        }
    }

    /**
     * Handles the "New Circuit" button action.
     * <p>
     * Displays a text input dialog to the user. If a valid name is provided,
     * a new circuit is created in the database under the current project,
     * and the list is refreshed.
     * </p>
     */
    @FXML
    private void handleNewCircuit() {
        TextInputDialog dialog = new TextInputDialog("Main");
        dialog.setTitle("New Circuit");
        dialog.setHeaderText("Create a New Circuit");
        dialog.setContentText("Circuit Name:");

        styleDialog(dialog.getDialogPane());

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            circuitDao.createCircuit(
                currentProject.getId(),
                result.get().trim()
            );
            refreshList();
        }
    }

    /**
     * Handles the "Open Circuit" button action.
     * <p>
     * Retrieves the currently selected circuit and triggers the transition
     * to the main editor view.
     * </p>
     */
    @FXML
    private void handleOpenCircuit() {
        Circuit selected = circuitList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openMainEditor(selected);
        }
    }

    /**
     * Handles the "Export Project" button action.
     * <p>
     * Reloads the project data to ensure the latest state is captured,
     * then executes the project-wide export to JPG function.
     * Displays a success message upon completion.
     * </p>
     */
    @FXML
    private void handleExport() {
        if (currentProject != null) {
            // Load fresh data to ensure we have all circuits and components
            currentProject.load();
            currentProject.export();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export Complete");
            alert.setHeaderText(null);
            alert.setContentText(
                "All circuits in the project have been exported to JPG files."
            );
            styleDialog(alert.getDialogPane());
            alert.showAndWait();
        }
    }

    /**
     * Handles the "Back" button action.
     * <p>
     * Navigates the user back to the initial Start View (Project Selection screen).
     * </p>
     */
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

    /**
     * Transitions the scene to the Main Circuit Editor.
     * <p>
     * Loads the {@code MainView.fxml}, initializes the {@link MainViewController},
     * and passes the current project and selected circuit context to it.
     * </p>
     *
     * @param circuit The circuit to be opened in the editor.
     */
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

    /**
     * Applies the application CSS styles to a dialog pane.
     *
     * @param dialogPane The dialog pane to style.
     */
    private void styleDialog(DialogPane dialogPane) {
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
