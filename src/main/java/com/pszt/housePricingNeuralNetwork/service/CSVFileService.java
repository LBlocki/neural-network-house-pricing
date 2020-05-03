package com.pszt.housePricingNeuralNetwork.service;

import java.io.File;

public interface CSVFileService {

    File getCurrentCSVFile();

    void saveNewCSVFile(File file);

}
