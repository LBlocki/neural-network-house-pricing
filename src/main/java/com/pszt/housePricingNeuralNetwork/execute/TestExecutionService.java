package com.pszt.housePricingNeuralNetwork.execute;

import com.pszt.housePricingNeuralNetwork.config.ApplicationBeansConfiguration;
import com.pszt.housePricingNeuralNetwork.logger.MessageProducer;
import io.vavr.control.Try;
import javafx.application.Platform;

import static com.pszt.housePricingNeuralNetwork.logger.MessageProducer.*;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TestExecutionService implements ExecutionService {

    private final MessageProducer messageProducer = ApplicationBeansConfiguration.getInstance(MessageProducer.class);
    private final BlockingQueue<ExecutionObserver> observers = new ArrayBlockingQueue<>(16);

    private static boolean EXECUTION_POSSIBLE = true;

    @Override
    public void addObserver(ExecutionObserver observer) {
        Try.run(() -> this.observers.add(observer)).onFailure(t -> messageProducer.addMessage(new Message(
                "Failed to add observable (" + observer.toString() + ") to queue. Reason:" + t.getMessage(),
                LOG_TYPE.DEBUG)))
                .onSuccess(t -> messageProducer.addMessage(new Message(
                        "Successfully added observable (" + observer.toString() + ") to queue.",
                        LOG_TYPE.INFO)));
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
            messageProducer.addMessage(new Message("Execution starting...", LOG_TYPE.INFO));
            Random random = new Random(System.currentTimeMillis());
            final String fileName = "logs_" + random.nextInt() + ".txt";

            try {
                EXECUTION_POSSIBLE = false;

                if (!messageProducer.enableLoggerFileOutput(fileName)) {
                    throw new IOException();
                }
                messageProducer.addMessage(new Message("Test execution starting", LOG_TYPE.INFO));
                for (int i = 0; i < 5; i++) {

                    if (this.observers.stream().anyMatch(ExecutionObserver::isAbortRequested)) {
                        messageProducer.addMessage(new Message("Execution abort requested...", LOG_TYPE.INFO));
                        throw new RuntimeException();
                    }

                    Message message = new Message("This is message number" + i, LOG_TYPE.INFO);
                    messageProducer.addMessage(message);
                    Thread.sleep(500);
                }

                messageProducer.addMessage(new Message("Ending test execution", LOG_TYPE.INFO));

                if (messageProducer.isLoggerFileOpened(fileName)) {
                    messageProducer.disableLoggerFileOutput(fileName);
                }
                messageProducer.addMessage(new Message("Execution ending...", LOG_TYPE.INFO));
                Platform.runLater(() -> this.observers.forEach(ExecutionObserver::reactToExecutionEnd));
                EXECUTION_POSSIBLE = true;

            } catch (Exception ex) {
                Message message = new Message("Failed to run execution", LOG_TYPE.WARN);
                messageProducer.addMessage(message);
                Platform.runLater(() -> this.observers.forEach(ExecutionObserver::reactToExecutionEnd));

                if (messageProducer.isLoggerFileOpened(fileName)) {
                    messageProducer.disableLoggerFileOutput(fileName);
                }

                EXECUTION_POSSIBLE = true;
            }
        });

        thread.setDaemon(true);
        thread.start();
    }
}
