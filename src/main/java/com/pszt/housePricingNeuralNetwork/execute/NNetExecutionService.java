package com.pszt.housePricingNeuralNetwork.execute;

import com.pszt.housePricingNeuralNetwork.config.ApplicationBeansConfiguration;
import com.pszt.housePricingNeuralNetwork.logger.MessageProducer;
import com.pszt.housePricingNeuralNetwork.model.BostonHouse;
import com.pszt.housePricingNeuralNetwork.model.CSVFile;
import com.pszt.housePricingNeuralNetwork.perceptron.Perceptron;
import com.pszt.housePricingNeuralNetwork.service.CSVFileService;
import io.vavr.control.Try;
import javafx.application.Platform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class NNetExecutionService implements ExecutionService {

    private final MessageProducer logger = ApplicationBeansConfiguration.getInstance(MessageProducer.class);
    private final BlockingQueue<ExecutionObserver> observers = new ArrayBlockingQueue<>(16);
    private final CSVFileService csvFileService = ApplicationBeansConfiguration.getInstance(CSVFileService.class);

    private static boolean EXECUTION_POSSIBLE = true;

    public Perceptron perceptron = null;

    @Override
    public void addObserver(ExecutionObserver observer) {
        Try.run(() -> this.observers.add(observer))
                .onFailure(t -> logger.error("Failed to add observable (" + observer.toString() + ") to queue. Reason:" + t.getMessage()))
                .onSuccess(t -> logger.info("Successfully added observable (" + observer.toString() + ") to queue."));
    }

    @Override
    public boolean canRunExecution() {
        return EXECUTION_POSSIBLE;
    }

    @Override
    public void execute() {
        if (!EXECUTION_POSSIBLE) {
            return;
        }
        Thread thread = new Thread(() -> {
            Platform.runLater(() -> this.observers.forEach(ExecutionObserver::reactToExecutionStart));
            logger.info("Execution starting...");
            final String fileName = "logs_" + this.getCurrentDateAsString() + ".txt";
            try {
                EXECUTION_POSSIBLE = false;

                File file = new File(getClass().getResource("/data/boston-house.csv").getFile());
                this.csvFileService.saveNewCSVFile(file);
                logger.info("Successfully read Boston House initial data");

                if (!logger.enableLoggerFileOutput(fileName)) {
                    throw new IOException();
                }

                logger.info("Creating perceptron...");
                List<BostonHouse> data = this.csvFileService.getCurrentCSVFile().getData();
                this.perceptron = new Perceptron(13, 14, 2, data);
                logger.info("Successfully created perceptron");

                logger.info("Perceptron execution starting...");
                this.perceptron.execute();
                logger.info("Perceptron execution ending...");

                if (logger.isLoggerFileOpened(fileName)) {
                    logger.disableLoggerFileOutput(fileName);
                }
                logger.info("Execution ending...");
                Platform.runLater(() -> this.observers.forEach(ExecutionObserver::reactToExecutionEnd));
                EXECUTION_POSSIBLE = true;

            } catch (Exception ex) {
                logger.error("Failed to run execution");
                Platform.runLater(() -> this.observers.forEach(ExecutionObserver::reactToExecutionEnd));

                if (logger.isLoggerFileOpened(fileName)) {
                    logger.disableLoggerFileOutput(fileName);
                }
                ex.printStackTrace();

                EXECUTION_POSSIBLE = true;
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    public void executePrediction() {
        if (!EXECUTION_POSSIBLE) {
            return;
        }
        if (this.perceptron == null) {
            this.logger.error("No model of perceptron. Please, click 'Start training' button.");
            return;
        }

        Thread thread = new Thread(() -> {
            Platform.runLater(() -> this.observers.forEach(ExecutionObserver::reactToExecutionStart));
            logger.info("Execution starting...");
            try {
                EXECUTION_POSSIBLE = false;
                CSVFile csvFile = this.csvFileService.getCurrentCSVFile();

                if (csvFile == null) {
                    throw new FileNotFoundException("No CSV file");
                }

                List<BostonHouse> dataset = csvFile.getData();
                for (int i = 0; i < dataset.size(); i++) {
                    Float predictedPrice = this.perceptron.predict(dataset.get(i));
                    this.logger.info(String.format("%d. Predicted price: %f", i + 1, predictedPrice));
                }

                logger.info("Execution ending...");
                Platform.runLater(() -> this.observers.forEach(ExecutionObserver::reactToExecutionEnd));
                EXECUTION_POSSIBLE = true;

            } catch (Exception ex) {
                logger.error("Failed to run execution");
                Platform.runLater(() -> this.observers.forEach(ExecutionObserver::reactToExecutionEnd));
                ex.printStackTrace();

                EXECUTION_POSSIBLE = true;
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    private String getCurrentDateAsString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
        return simpleDateFormat.format(new Date());
    }
}
