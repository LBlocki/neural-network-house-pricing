package com.pszt.housePricingNeuralNetwork.execute;

import com.pszt.housePricingNeuralNetwork.config.ApplicationBeansConfiguration;
import com.pszt.housePricingNeuralNetwork.logger.MessageProducer;
import io.vavr.control.Try;
import javafx.application.Platform;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TestExecutionService implements ExecutionService {

    private final MessageProducer logger = ApplicationBeansConfiguration.getInstance(MessageProducer.class);
    private final BlockingQueue<ExecutionObserver> observers = new ArrayBlockingQueue<>(16);

    private static boolean EXECUTION_POSSIBLE = true;

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

                if (!logger.enableLoggerFileOutput(fileName)) {
                    throw new IOException();
                }
                logger.info("Test execution starting");
                for (int i = 0; i < 5; i++) {

                    if (this.observers.stream().anyMatch(ExecutionObserver::isAbortRequested)) {
                        logger.info("Execution abort requested...");
                        throw new RuntimeException();
                    }

                    logger.info("This is message number " + i);
                    Thread.sleep(500);
                }

                logger.info("Ending test execution");

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
