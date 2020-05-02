package com.pszt.housePricingNeuralNetwork.service;

import javafx.scene.image.Image;

import java.io.File;

public interface PictureService {

    Image getCurrentImage();

    void saveNewPicture(File file);

}
