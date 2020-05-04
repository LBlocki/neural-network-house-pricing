package com.pszt.housePricingNeuralNetwork.service;

import com.pszt.housePricingNeuralNetwork.config.ApplicationBeansConfiguration;
import com.pszt.housePricingNeuralNetwork.logger.MessageProducer;
import com.pszt.housePricingNeuralNetwork.repository.CSVFileRepository;

import java.io.File;

import static com.pszt.housePricingNeuralNetwork.logger.MessageProducer.*;

public class CSVFileServiceImpl implements CSVFileService {

    private final MessageProducer messageProducer = ApplicationBeansConfiguration.getInstance(MessageProducer.class);
    private final CSVFileRepository csvFileRepository =
            ApplicationBeansConfiguration.getInstance(CSVFileRepository.class);

    @Override
    public File getCurrentCSVFile() {
        return this.csvFileRepository
                .getCurrentCSVFile()
                .onFailure(t -> this.messageProducer
                        .addMessage(
                                new Message("Failed to fetch current csv file:" + t.getMessage(), LOG_TYPE.WARN)
                        )
                )
                .onSuccess(t -> this.messageProducer
                        .addMessage(
                                new Message("Successfully fetched current csv file", LOG_TYPE.INFO)
                        )
                )
                .getOrNull();
    }

    @Override
    public void saveNewCSVFile(File file) {

        String fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        if (!fileExtension.equals("csv")) {
            this.messageProducer
                    .addMessage(
                            new Message("Chosen file has incorrect extension (" + fileExtension + ")." +
                                    " File must have 'csv' extension.", LOG_TYPE.WARN)
                    );
        } else {
            this.csvFileRepository
                    .saveNewCSVFile(file)
                    .onFailure(t -> this.messageProducer
                            .addMessage(
                                    new Message("Failed to save new file: " + t.getMessage(), LOG_TYPE.WARN)
                            )
                    )
                    .onSuccess(t -> this.messageProducer
                            .addMessage(
                                    new Message("Successfully saved new file: " + file.getName(), LOG_TYPE.INFO)
                            )
                    );
        }
    }
}
