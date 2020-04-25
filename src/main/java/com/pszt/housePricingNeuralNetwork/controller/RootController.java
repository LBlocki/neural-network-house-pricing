package com.pszt.housePricingNeuralNetwork.controller;

import com.pszt.housePricingNeuralNetwork.config.ApplicationBeansConfiguration;
import com.pszt.housePricingNeuralNetwork.service.LoggerService;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 *  Glowny kontroller dla widoku
 *  todo zrefaktorowac w celu zwiekszenia liczby modulow. To jest trzeba bedzie zrobic osobny kontroller
 *      wraz z osobnym widokiem dla niej. Tak samo dla przyciskow + obrazka
 */
public class RootController {

    private final LoggerService loggerService = ApplicationBeansConfiguration.getInstance(LoggerService.class);

    @FXML
    public TextArea console;

    @FXML
    public void initialize() {
        console.textProperty().addListener(val -> console.setScrollTop(Double.MAX_VALUE));
        loggerService.setLoggerOutputToTextArea(console);
        console.setWrapText(true);
    }
}
