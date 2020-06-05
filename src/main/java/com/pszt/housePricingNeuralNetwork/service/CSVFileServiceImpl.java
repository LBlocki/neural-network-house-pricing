package com.pszt.housePricingNeuralNetwork.service;

import com.pszt.housePricingNeuralNetwork.config.ApplicationBeansConfiguration;
import com.pszt.housePricingNeuralNetwork.logger.MessageProducer;
import com.pszt.housePricingNeuralNetwork.model.CSVFile;
import com.pszt.housePricingNeuralNetwork.repository.CSVFileRepository;

import java.io.File;

public class CSVFileServiceImpl implements CSVFileService {

    private final MessageProducer logger = ApplicationBeansConfiguration.getInstance(MessageProducer.class);
    private final CSVFileRepository csvFileRepository =
            ApplicationBeansConfiguration.getInstance(CSVFileRepository.class);

    @Override
    public CSVFile getCurrentCSVFile() {
        return this.csvFileRepository
                .getCurrentCSVFile()
                .onFailure(t -> this.logger.error("Failed to fetch current csv file:" + t.getMessage()))
                .onSuccess(t -> this.logger.info("Successfully fetched current csv file"))
                .getOrNull();
    }

    @Override
    public void saveNewCSVFile(File file) {

        String fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        if (!fileExtension.equals("csv")) {
            this.logger.warn("Chosen file has incorrect extension (" + fileExtension + "). File must have 'csv' extension.");

        } else {
            this.csvFileRepository
                    .saveNewCSVFile(file)
                    .onFailure(t -> this.logger.error("Failed to save new file: " + t.getMessage()))
                    .onSuccess(t -> this.logger.info("Successfully saved new file: " + file.getName()));
        }
    }
}
