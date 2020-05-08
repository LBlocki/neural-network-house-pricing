package com.pszt.housePricingNeuralNetwork.controller;

import com.pszt.housePricingNeuralNetwork.HousePricingNeuralNetwork;
import com.pszt.housePricingNeuralNetwork.config.ApplicationBeansConfiguration;
import com.pszt.housePricingNeuralNetwork.execute.ExecutionService;
import com.pszt.housePricingNeuralNetwork.service.LoggerService;
import com.pszt.housePricingNeuralNetwork.service.CSVFileService;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Glowny kontroller dla widoku
 * todo zrefaktorowac w celu zwiekszenia liczby modulow. To jest trzeba bedzie zrobic osobny kontroller
 * wraz z osobnym widokiem dla niej. Tak samo dla przyciskow + obrazka
 */
public class RootController implements ExecutionService.ExecutionObserver {

    private final LoggerService loggerService = ApplicationBeansConfiguration.getInstance(LoggerService.class);
    private final ExecutionService execution = ApplicationBeansConfiguration.getInstance(ExecutionService.class);
    private final CSVFileService csvFileService =
            ApplicationBeansConfiguration.getInstance(CSVFileService.class);
    private final FileChooser fileChooser = new FileChooser();

    private static boolean SHOULD_ABORT = false;

    @FXML
    public TextArea console;
    @FXML
    public Group buttonGroup;

    public RootController() {
        this.execution.addObserver(this);
    }

    @FXML
    public void initialize() {
        this.console.textProperty().addListener(val -> console.setScrollTop(Double.MAX_VALUE));
        this.loggerService.setLoggerOutputToTextArea(console);
        this.console.setWrapText(true);
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
        console.clear();
    }

    @Override
    public void reactToExecutionStart() {
        getListOfButtons().forEach(button -> {
            if (!button.getText().equals("Start calculations")) {
                button.setDisable(true);
            } else {
                button.setText("Stop calculations");
            }
        });
    }

    @Override
    public void reactToExecutionEnd() {
        getListOfButtons().forEach(button -> {
            if (button.getText().equals("Stop calculations")) {
                button.setText("Start calculations");
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
