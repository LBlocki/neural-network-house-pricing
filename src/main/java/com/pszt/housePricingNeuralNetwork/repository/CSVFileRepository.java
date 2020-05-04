package com.pszt.housePricingNeuralNetwork.repository;

import io.vavr.control.Try;

import java.io.File;

public interface CSVFileRepository {

    Try<Void> saveNewCSVFile(File file);

    Try<File> getCurrentCSVFile();
}
