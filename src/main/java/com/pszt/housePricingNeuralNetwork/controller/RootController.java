package com.pszt.housePricingNeuralNetwork.controller;

import com.pszt.housePricingNeuralNetwork.HousePricingNeuralNetwork;
import com.pszt.housePricingNeuralNetwork.config.ApplicationBeansConfiguration;
import com.pszt.housePricingNeuralNetwork.execute.Execution;
import com.pszt.housePricingNeuralNetwork.service.LoggerService;
import com.pszt.housePricingNeuralNetwork.service.CSVFileService;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.File;

/**
 *  Glowny kontroller dla widoku
 *  todo zrefaktorowac w celu zwiekszenia liczby modulow. To jest trzeba bedzie zrobic osobny kontroller
 *      wraz z osobnym widokiem dla niej. Tak samo dla przyciskow + obrazka
 */
public class RootController {

    private final LoggerService loggerService = ApplicationBeansConfiguration.getInstance(LoggerService.class);
    private final Execution execution = ApplicationBeansConfiguration.getInstance(Execution.class);
    private final CSVFileService csvFileService =
            ApplicationBeansConfiguration.getInstance(CSVFileService.class);

    private  final FileChooser fileChooser = new FileChooser();

    @FXML
    public TextArea console;

    @FXML
    public void initialize() {
        this.console.textProperty().addListener(val -> console.setScrollTop(Double.MAX_VALUE));
        this.loggerService.setLoggerOutputToTextArea(console);
        this.console.setWrapText(true);
    }

    public void importNewFileClicked() {
        File file = this.fileChooser.showOpenDialog(HousePricingNeuralNetwork.getStage());
        if(file != null) {
            this.csvFileService.saveNewCSVFile(file);
        }
    }

    public void startClicked() {
        execution.testExecute();
    }
}
