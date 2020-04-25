package com.pszt.housePricingNeuralNetwork.service;

import com.pszt.housePricingNeuralNetwork.logger.CustomStaticOutputStreamAppender;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Implementacja dla serwisu Loger ustawiajaca output z logow do textArea w konsoli
 */
public class LoggerServiceImpl implements LoggerService {

    public void setLoggerOutputToTextArea(TextArea textArea) {
        OutputStream os = new TextAreaOutputStream(textArea);
        CustomStaticOutputStreamAppender.setStaticOutputStream(os);
    }

    @AllArgsConstructor
    private static class TextAreaOutputStream extends OutputStream {

        private TextArea textArea;

        @Override
        public void write(int b) throws IOException {
            Platform.runLater(() -> {
                textArea.appendText(String.valueOf((char)b));
                textArea.setScrollTop(Double.MAX_VALUE);
            });
        }
    }
}
