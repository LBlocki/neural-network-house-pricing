package com.pszt.housePricingNeuralNetwork.perceptron;

import com.pszt.housePricingNeuralNetwork.config.ApplicationBeansConfiguration;
import com.pszt.housePricingNeuralNetwork.logger.MessageProducer;
import com.pszt.housePricingNeuralNetwork.model.BostonHouse;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ToString
public class Perceptron {
    private final MessageProducer logger = ApplicationBeansConfiguration.getInstance(MessageProducer.class);

    final float WEIGTH_SPAN = 1f;
    final float LEARNING_RATE = 0.08f;
    final Integer NUMBER_OF_EPOCH = 20;

    private List<Float> inputLayers;
    private List<Float> hiddenLayers;
    private List<Float> weigths;
    private List<BostonHouse> trainingData;
    private Integer bias;
    private Integer inputSize;
    private float value = 0f;
    private float sum = 0f;
    private float error = 0f;

    public Perceptron(Integer input, Integer hidden, Integer bias, List<BostonHouse> data) throws IllegalAccessException {
        this.bias = bias;
        this.inputSize = input + bias;
        this.inputLayers = new ArrayList<>(this.inputSize);
        this.hiddenLayers = new ArrayList<>(hidden);
        this.weigths = new ArrayList<>(this.inputSize);

        Random rnd = new Random();
        for (int i = 0; i < this.inputSize; i++) {
            this.weigths.add(rnd.nextFloat()%2);
        }

        logger.info("Normalizing training data...");
        this.trainingData = Perceptron.normalizeDataset(data);
        logger.info("Successfully normalized training data");
    }

    public void execute() {
        for (int i = 0; i < NUMBER_OF_EPOCH; i++)
        {
            float accError = 0;
            float hit = 0;

            for (BostonHouse trainingDatum : trainingData) {
                this.inputLayers = trainingDatum.getListOfValues();

                float result = this.calculateValue();

                if (result / trainingDatum.getMEDV() > 0.5f)
                    hit++;

                accError += this.calculateError(trainingDatum.getMEDV());
                this.train(trainingDatum.getMEDV());
            }

            logger.trace(String.format("TOTAL ERROR AFTER: { %d }\tprecent error: { %f }\tsquare error: { %f }", i, (hit / trainingData.size()), accError));
            logger.trace(this.weigths.toString());
        }

    }

    public float calculateValue() {
        sum = 0f;

        for (int i = 0; i < this.inputSize; i++) {
            sum += this.inputLayers.get(i) * this.weigths.get(i);
        }

        value = activate(sum);
        return value;
    }

    public void train(float expectedValue) {
        float derivateOfLossFunction = value - expectedValue;
        float derivateOfPerceptron = derivateOfLossFunction * derivate(sum);

        for (int i = 0; i < this.inputLayers.size(); i++) {
            float derivateOfWeigth = derivateOfPerceptron * this.inputLayers.get(i);
            this.weigths.set(i, this.weigths.get(i) - LEARNING_RATE * derivateOfWeigth);
        }
    }

    public float calculateError(float expectedValue) {
        error = (expectedValue - value) * (expectedValue - value) / 2f;
        return error;
    }

    public float activate(float value) {
        return (float) (1f / (1f + Math.exp(-value)));
    }

    public float derivate(float value) {
        float act = activate(value);

        return act * (1f - act);
    }

    // normalize values of data set mapping them to range [0, 1]
    public static List<BostonHouse> normalizeDataset(List<BostonHouse> dataset) throws IllegalAccessException {
        List<Float> values = dataset.stream()
                .map(BostonHouse::getListOfValues).collect(ArrayList::new, List::addAll, List::addAll);
        Float min = values.stream().min(Float::compare).get();
        Float max = values.stream().max(Float::compare).get();
        for (BostonHouse data : dataset) {
            BostonHouse.normalize(data, min, max);
        }
        return dataset;
    }
}
