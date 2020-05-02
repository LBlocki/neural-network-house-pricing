package com.pszt.housePricingNeuralNetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Getter;

/**
 * <p>
 * Projekt sieci neuronowej do wyceny domow.
 * #PSZT
 *
 * @author Lukasz Blocki, Kamila Szymczuk
 * @version 1.0
 * @since 2020.04.21
 */
public class HousePricingNeuralNetwork extends Application {

    @Getter
    private static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * @param primaryStage dostarczana przez JavaFX.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/View.fxml"));

        Scene scene = new Scene(root, 1000, 600, Color.WHITE);

        primaryStage.setTitle("Neural Network for house pricing");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        stage = primaryStage;
    }
}
