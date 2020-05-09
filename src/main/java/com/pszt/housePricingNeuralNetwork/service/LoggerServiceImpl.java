package com.pszt.housePricingNeuralNetwork.service;

import com.pszt.housePricingNeuralNetwork.logger.CustomStaticOutputStreamAppender;
import com.pszt.housePricingNeuralNetwork.logger.MessageProducer;
import javafx.application.Platform;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Implementacja dla serwisu Loger ustawiajaca output z logow do textArea w konsoli
 */
public class LoggerServiceImpl implements LoggerService {

    public void setLoggerOutputToTextFlow(TextFlow textFlow) {
        OutputStream os = new TextAreaOutputStream(textFlow, "");
        CustomStaticOutputStreamAppender.setStaticOutputStream(os);
    }

    @AllArgsConstructor
    private static class TextAreaOutputStream extends OutputStream {

        private TextFlow textFlow;
        private String buffer;

        @Override
        public void write(int b) throws IOException {
            buffer += (char) b;
            if (buffer.endsWith("\n")) {
                flushToTextField();
            }
        }

        private void flushToTextField() {
            Arrays.stream(MessageProducer.LOG_TYPE.values())
                    .filter(value -> buffer.contains(value.toString()))
                    .findAny()
                    .ifPresent(value -> {
                        Text text = new Text();
                        text.setText(buffer);
                        text.setFill(MessageProducer.getLogColor(value));
                        Platform.runLater(() -> textFlow.getChildren().add(text));
                    });

            buffer = "";
        }
    }
}
