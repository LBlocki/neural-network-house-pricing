package com.pszt.housePricingNeuralNetwork.repository;

import com.opencsv.bean.CsvToBeanBuilder;
import com.pszt.housePricingNeuralNetwork.model.BostonHouse;
import com.pszt.housePricingNeuralNetwork.model.CSVFile;
import io.vavr.control.Try;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.FileReader;
import java.util.List;

public class CSVFileRepositoryImpl implements CSVFileRepository {

    private CSVFile csvFile;

    @Override
    public Try<Void> saveNewCSVFile(File file) {
        return Try.run(() -> {
            Validate.notNull(file);
            List<BostonHouse> beans = new CsvToBeanBuilder<BostonHouse>(new FileReader(file)).withType(BostonHouse.class)
                    .withSeparator(';').build().parse();
            this.csvFile = new CSVFile(file, beans);
        });
    }

    @Override
    public Try<CSVFile> getCurrentCSVFile() {
        return Try.of(() -> {
            Validate.notNull(this.csvFile);
            return this.csvFile;
        });
    }
}
