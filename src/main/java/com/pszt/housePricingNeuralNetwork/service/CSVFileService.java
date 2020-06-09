package com.pszt.housePricingNeuralNetwork.service;

import com.pszt.housePricingNeuralNetwork.model.CSVFile;

import java.io.File;

public interface CSVFileService {

    CSVFile getCurrentCSVFile();

    void saveNewCSVFile(File file);

}
