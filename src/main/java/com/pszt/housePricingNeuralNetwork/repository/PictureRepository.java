package com.pszt.housePricingNeuralNetwork.repository;

import io.vavr.control.Try;
import javafx.scene.image.Image;
import lombok.NonNull;
import lombok.Value;

import java.nio.file.Path;

public interface PictureRepository {

    Try<Void> saveNewPicture(PictureRequest pictureRequest);

    Try<PictureResponse> getCurrentImage();

    @Value
    class PictureRequest {
        @NonNull Path picturePath;
    }

    @Value
    class PictureResponse {
        @NonNull Image image;
    }
}
