package com.pszt.housePricingNeuralNetwork.logger;

import io.vavr.control.Try;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Producent wiadomosci odpowiedzialny za wyswietlanie dokladanych do kolejki kolejnych wiadomosci z dowolnych watkow
 * w taki sposob, aby zachowac synchronizacje miedzy watkami
 */
public class MessageProducer implements Runnable {

    private final BlockingQueue<Message> queue = new ArrayBlockingQueue<>(30);
    private final static Logger logger = LoggerFactory.getLogger(MessageProducer.class);

    private final FileLogger fileLogger = new FileLogger();

    private static boolean useFileOutput = false;

    public MessageProducer() {
        new Thread(this).start();
    }

    public void addMessage(Message message) {
        if (queue.size() < 30) {
            queue.offer(message);
        }
    }

    public boolean enableLoggerFileOutput(String fileName) {
        return this.fileLogger.openFileLogger(fileName);
    }

    public void disableLoggerFileOutput(String fileName) {
        this.fileLogger.closeFileLogger(fileName);
    }

    public boolean isLoggerFileOpened(String fileName) {
        return this.fileLogger.isFileOpened(fileName);
    }

    @Override
    public void run() {
        Try.run(() -> {
            while (true) {
                if (!queue.isEmpty()) {
                    final Message message = queue.take();

                    if (useFileOutput) {
                        fileLogger.writeToLoggerFile(message.text);
                    } else {
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
                    }
                } else {
                    Thread.sleep(100);
                }
            }
        }).onFailure(t -> logger.warn("Failed to serve message query" + t.getMessage()));
    }

    @Value
    public static class Message {
        @NonNull String text;
        @NonNull LOG_TYPE log_type;
    }

    public enum LOG_TYPE {INFO, WARN, DEBUG, TRACE, ERROR}

    private static class FileLogger {

        private FileWriter fileWriter;
        private String currentFile;
        private static String dirName = "logs";

        public boolean openFileLogger(String fileName) {
            return Try.of(() -> {
                Validate.notBlank(fileName);
                Validate.isTrue(fileWriter == null, "File is already opened.");

                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
                if (!fileExtension.equals("txt")) {
                    throw new RuntimeException("Invalid file extension. Only txt logger files are allowed");
                }

                final File file = new File(System.getProperty("user.dir") + File.separator +
                        dirName + File.separator + fileName);
                if (!file.getParentFile().exists()) {
                    if (!file.getParentFile().mkdirs()) {
                        throw new IOException("Failed to create " + dirName + " directory.");
                    }
                }

                if (!file.createNewFile()) {
                    throw new IOException("Failed to create new file: " + fileName);
                }

                this.fileWriter = new FileWriter(file);

                useFileOutput = true;
                this.currentFile = fileName;

                return true;
            })
                    .onFailure(t -> logger.warn("[WARN] Failed to open file for logging. Reason: " + t.getMessage()))
                    .onSuccess(t -> logger.info("[INFO] Successfully opened file " + fileName + " for logging"))
                    .getOrElse(false);

        }

        public void writeToLoggerFile(String message) {

            Try.run(() -> this.fileWriter.write(message + "\n"))
                    .onFailure(t -> logger.warn("Failed to log message to file"));
        }

        public void closeFileLogger(String fileName) {

            Try.run(() -> {
                Validate.notNull(this.fileWriter, "No file is opened");
                Validate.isTrue(this.currentFile.equals(fileName),
                        "Given file name (" + fileName +
                                ") does not matched opened log file " + this.currentFile);

                this.fileWriter.close();
                this.fileWriter = null;
                useFileOutput = false;
            })
                    .onFailure(t -> logger.warn("[WARN] Failed to close file " + fileName))
                    .onSuccess(t -> logger.info("[INFO] Successfully closed file " + fileName));
        }

        public boolean isFileOpened(String fileName) {
            return Try.of(() -> {
                Validate.notBlank(fileName);

                return this.fileWriter != null && this.currentFile.equals(fileName);

            })
                    .onFailure(t -> logger.warn("[WARN] Unable to check if file is opened. Given file name is blank."))
                    .getOrElse(false);
        }
    }
}
