package com.pszt.housePricingNeuralNetwork.repository;

import com.pszt.housePricingNeuralNetwork.model.Picture;
import io.vavr.control.Try;
import javafx.scene.image.Image;
import org.apache.commons.lang3.Validate;

public class PictureRepositoryImpl implements PictureRepository {

    private Picture picture;

    @Override
    public Try<Void> saveNewPicture(PictureRequest pictureRequest) {
        return Try.run(() -> {
            Validate.notNull(pictureRequest);
            Validate.notBlank(pictureRequest.getPicturePath().toString());

            this.picture = Picture.builder()
                    .image(new Image(pictureRequest.getPicturePath().toUri().toString()))
                    .filePath(pictureRequest.getPicturePath())
                    .build();
        });
    }

    @Override
    public Try<PictureResponse> getCurrentImage() {
        return Try.of(() -> {
            Validate.notNull(this.picture);
            Validate.notNull(this.picture.getImage());
            return new PictureResponse(this.picture.getImage());
        });
    }
}
