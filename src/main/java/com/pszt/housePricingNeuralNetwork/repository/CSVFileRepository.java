package com.pszt.housePricingNeuralNetwork.repository;

import com.pszt.housePricingNeuralNetwork.model.CSVFile;
import io.vavr.control.Try;

import java.io.File;

public interface CSVFileRepository {

    Try<Void> saveNewCSVFile(File file);

    Try<CSVFile> getCurrentCSVFile();
}
