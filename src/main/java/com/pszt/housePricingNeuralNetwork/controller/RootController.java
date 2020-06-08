package com.pszt.housePricingNeuralNetwork.controller;

import com.pszt.housePricingNeuralNetwork.HousePricingNeuralNetwork;
import com.pszt.housePricingNeuralNetwork.config.ApplicationBeansConfiguration;
import com.pszt.housePricingNeuralNetwork.execute.ExecutionService;
import com.pszt.housePricingNeuralNetwork.service.CSVFileService;
import com.pszt.housePricingNeuralNetwork.service.LoggerService;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Glowny kontroler dla widoku
 */
public class RootController implements ExecutionService.ExecutionObserver {

    private final LoggerService loggerService = ApplicationBeansConfiguration.getInstance(LoggerService.class);
    private final ExecutionService execution = ApplicationBeansConfiguration.getInstance(ExecutionService.class);
    private final CSVFileService csvFileService =
            ApplicationBeansConfiguration.getInstance(CSVFileService.class);
    private final FileChooser fileChooser = new FileChooser();

    private static boolean SHOULD_ABORT = false;

    @FXML
    public TextFlow console;
    @FXML
    public Group buttonGroup;
    @FXML
    public ScrollPane scrollPane;

    public RootController() {
        this.execution.addObserver(this);
    }

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(console.heightProperty());
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.loggerService.setLoggerOutputToTextFlow(console);
    }

    public void importNewFileClicked() {
        File file = this.fileChooser.showOpenDialog(HousePricingNeuralNetwork.getStage());
        if (file != null) {
            this.csvFileService.saveNewCSVFile(file);
        }
    }

    public void startClicked() {
        if (execution.canRunExecution()) {
            SHOULD_ABORT = false;
            execution.execute();
        } else {
            SHOULD_ABORT = true;
            getListOfButtons().forEach(button -> button.setDisable(true));
        }
    }

    public void clearConsoleClicked() {
        console.getChildren().clear();
    }

    @Override
    public void reactToExecutionStart() {
        getListOfButtons().forEach(button -> {
            if (!button.getText().equals("Start training")) {
                button.setDisable(true);
            } else {
                button.setText("Stop training");
            }
        });
    }

    @Override
    public void reactToExecutionEnd() {
        getListOfButtons().forEach(button -> {
            if (button.getText().equals("Stop training")) {
                button.setText("Start training");
            }
            button.setDisable(false);
        });
        SHOULD_ABORT = false;
    }

    @Override
    public boolean isAbortRequested() {
        return SHOULD_ABORT;
    }

    private List<Button> getListOfButtons() {
        List<Button> buttons = new ArrayList<>();
        VBox vBox = (VBox) this.buttonGroup.getChildren().stream().findFirst().orElseThrow(RuntimeException::new);
        vBox.getChildren().forEach(child -> {
            Button button = (Button) child;
            buttons.add(button);
        });

        return buttons;
    }
}
