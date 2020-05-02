package com.pszt.housePricingNeuralNetwork.controller;

import com.pszt.housePricingNeuralNetwork.HousePricingNeuralNetwork;
import com.pszt.housePricingNeuralNetwork.config.ApplicationBeansConfiguration;
import com.pszt.housePricingNeuralNetwork.service.LoggerService;
import com.pszt.housePricingNeuralNetwork.service.PictureService;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

/**
 *  Glowny kontroller dla widoku
 *  todo zrefaktorowac w celu zwiekszenia liczby modulow. To jest trzeba bedzie zrobic osobny kontroller
 *      wraz z osobnym widokiem dla niej. Tak samo dla przyciskow + obrazka
 */
public class RootController {

    private final LoggerService loggerService = ApplicationBeansConfiguration.getInstance(LoggerService.class);

    private final PictureService pictureService =
            ApplicationBeansConfiguration.getInstance(PictureService.class);

    private  final FileChooser fileChooser = new FileChooser();

    @FXML
    public TextArea console;

    @FXML
    public ImageView imageView;

    @FXML
    public void initialize() {
        this.console.textProperty().addListener(val -> console.setScrollTop(Double.MAX_VALUE));
        this.loggerService.setLoggerOutputToTextArea(console);
        this.console.setWrapText(true);
    }

    public void importNewPictureClicked() {
        File file = this.fileChooser.showOpenDialog(HousePricingNeuralNetwork.getStage());
        if(file != null) {
            this.pictureService.saveNewPicture(file);

            Image image = this.pictureService.getCurrentImage();

            if(image != null) {
                this.imageView.setImage(image);
            }
        }
    }
}
