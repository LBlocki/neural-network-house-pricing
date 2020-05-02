package com.pszt.housePricingNeuralNetwork.logger;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Producent wiadomosci odpowiedzialny za wyswietlanie dokladanych do kolejki kolejnych wiadomosci z dowolnych watkow
 * w taki sposob, aby zachowac synchronizacje miedzy watkami
 */
public class MessageProducer implements Runnable {

    private final BlockingQueue<Message> queue = new ArrayBlockingQueue<>(30);
    private final static Logger logger = LoggerFactory.getLogger(MessageProducer.class);

    public MessageProducer() {
        new Thread(this).start();
    }

    public void addMessage(Message message) {
        if(queue.size() < 30) {
            queue.offer(message);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!queue.isEmpty()) {

                    final Message message = queue.take();
                    switch (message.log_type) {
                        case INFO:
                            logger.info("[INFO] " + message.text);
                            break;
                        case WARN:
                            logger.warn("[WARN] " + message.text);
                            break;
                        case DEBUG:
                            logger.debug("[DEBUG] " + message.text);
                            break;
                        case TRACE:
                            logger.trace("[TRACE] " + message.text);
                            break;
                        case ERROR:
                            logger.error("[ERROR] " + message.text);
                            break;
                    }

                } else {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Value
    @Builder
    public static class Message {
        @NonNull String text;
        @NonNull LOG_TYPE log_type;
    }

    public enum LOG_TYPE {INFO, WARN, DEBUG, TRACE, ERROR}
}
