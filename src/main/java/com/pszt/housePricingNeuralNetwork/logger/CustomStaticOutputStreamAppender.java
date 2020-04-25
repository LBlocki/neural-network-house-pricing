package com.pszt.housePricingNeuralNetwork.logger;

import ch.qos.logback.core.OutputStreamAppender;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Customowy appender dla outputu logow, stworzony, aby umozliwic poprzez modyfikacje streamu
 * logi do textArea w aplikacji zamiast na wyjscie standardowe. Jego konfiguracja znajduje sie w pliku logback.xml
 */
public class CustomStaticOutputStreamAppender<T> extends OutputStreamAppender<T> {

    private static final DelegatingOutputStream DELEGATING_OUTPUT_STREAM = new DelegatingOutputStream(null);

    @Override
    public void start() {
        setOutputStream(DELEGATING_OUTPUT_STREAM);
        super.start();
    }

    public static void setStaticOutputStream(OutputStream outputStream) {
        DELEGATING_OUTPUT_STREAM.setOutputStream(outputStream);
    }

    private static class DelegatingOutputStream extends FilterOutputStream {
        public DelegatingOutputStream(OutputStream out) {
            super(new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                }
            });
        }
        void setOutputStream(OutputStream outputStream) {
            this.out = outputStream;
        }
    }

}