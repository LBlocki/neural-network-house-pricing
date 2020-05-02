package com.pszt.housePricingNeuralNetwork.service;

import com.pszt.housePricingNeuralNetwork.config.ApplicationBeansConfiguration;
import com.pszt.housePricingNeuralNetwork.logger.MessageProducer;
import com.pszt.housePricingNeuralNetwork.repository.PictureRepository;
import javafx.scene.image.Image;

import java.io.File;
import java.nio.file.Paths;

import static com.pszt.housePricingNeuralNetwork.repository.PictureRepository.*;
import static com.pszt.housePricingNeuralNetwork.logger.MessageProducer.*;

public class PictureServiceImpl implements PictureService {

    private final MessageProducer messageProducer = ApplicationBeansConfiguration.getInstance(MessageProducer.class);
    private final PictureRepository pictureRepository =
            ApplicationBeansConfiguration.getInstance(PictureRepository.class);

    @Override
    public Image getCurrentImage() {
        PictureResponse response = this.pictureRepository
                .getCurrentImage()
                .onFailure(t -> this.messageProducer
                        .addMessage(Message.builder()
                                .text("Failed to fetch current picture:" + t.getMessage())
                                .log_type(LOG_TYPE.WARN)
                                .build()
                        )
                )
                .onSuccess(t -> this.messageProducer
                        .addMessage(Message.builder()
                                .text("Successfully fetched current picture")
                                .log_type(LOG_TYPE.INFO)
                                .build()
                        )
                )
                .getOrNull();

        return response != null ? response.getImage() : null;
    }

    @Override
    public void saveNewPicture(File file) {

        String fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        if (!fileExtension.equals("jpeg") && !fileExtension.equals("png")) {
            this.messageProducer
                    .addMessage(Message.builder()
                            .text("Chosen file has incorrect extension. Possible file types are jpeg and png.")
                            .log_type(LOG_TYPE.WARN)
                            .build()
                    );
        } else {
            final PictureRequest request = new PictureRequest(Paths.get(file.toURI()));

            this.pictureRepository
                    .saveNewPicture(request)
                    .onFailure(t -> this.messageProducer
                            .addMessage(Message.builder()
                                    .text("Failed to save new picture:" + t.getMessage())
                                    .log_type(LOG_TYPE.WARN)
                                    .build()
                            )
                    )
                    .onSuccess(t -> this.messageProducer
                            .addMessage(Message.builder()
                                    .text("Successfully saved new picture")
                                    .log_type(LOG_TYPE.INFO)
                                    .build()
                            )
                    );
        }
    }
}
