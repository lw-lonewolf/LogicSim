package com.logisim.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
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
    public void initialize() {
        System.out.println("MainViewController initialized");
    }
}
