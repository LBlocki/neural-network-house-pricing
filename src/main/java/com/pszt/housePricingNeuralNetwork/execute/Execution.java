package com.pszt.housePricingNeuralNetwork.execute;

import com.pszt.housePricingNeuralNetwork.config.ApplicationBeansConfiguration;
import com.pszt.housePricingNeuralNetwork.logger.MessageProducer;

import static com.pszt.housePricingNeuralNetwork.logger.MessageProducer.*;

import java.io.IOException;
import java.util.Random;

public class Execution {

    private final MessageProducer messageProducer = ApplicationBeansConfiguration.getInstance(MessageProducer.class);

    public void testExecute() {
        Thread thread = new Thread(() -> {

            Random random = new Random(System.currentTimeMillis());
            final String fileName = "logs_" + random.nextInt() + ".txt";

            try {

                if(!messageProducer.enableLoggerFileOutput(fileName)) {
                    throw new IOException();
                }

                for (int i = 0; i < 5; i++) {

                    Message message = Message.builder().text("This is message number" + i).log_type(LOG_TYPE.INFO).build();
                    messageProducer.addMessage(message);
                    Thread.sleep(500);
                }

                if(messageProducer.isLoggerFileOpened(fileName)) {
                    messageProducer.disableLoggerFileOutput(fileName);
                }

            } catch (Exception ex) {
                Message message = Message.builder().text("Failed to run execution").log_type(LOG_TYPE.WARN).build();
                messageProducer.addMessage(message);
                if(messageProducer.isLoggerFileOpened(fileName)) {
                    messageProducer.disableLoggerFileOutput(fileName);
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

}