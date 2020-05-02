package com.pszt.housePricingNeuralNetwork.model;

import javafx.scene.image.Image;
import lombok.Builder;
import lombok.Data;

import java.nio.file.Path;

@Data
@Builder
public class Picture {
    Image image;
    Path filePath;
}
