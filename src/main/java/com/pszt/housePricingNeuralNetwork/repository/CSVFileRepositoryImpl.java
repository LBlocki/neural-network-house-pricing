package com.pszt.housePricingNeuralNetwork.repository;

import com.pszt.housePricingNeuralNetwork.model.CSVFile;
import io.vavr.control.Try;
import org.apache.commons.lang3.Validate;

import java.io.File;

public class CSVFileRepositoryImpl implements CSVFileRepository {

    private CSVFile csvFile;

    @Override
    public Try<Void> saveNewCSVFile(File file) {
        return Try.run(() -> {
            Validate.notNull(file);
            this.csvFile = new CSVFile(file);
        });
    }

    @Override
    public Try<File> getCurrentCSVFile() {
        return Try.of(() -> {
            Validate.notNull(this.csvFile);
            return this.csvFile.getFile();
        });
    }
}
