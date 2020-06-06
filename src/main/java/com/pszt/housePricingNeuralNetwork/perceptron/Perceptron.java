package com.pszt.housePricingNeuralNetwork.perceptron;

import com.pszt.housePricingNeuralNetwork.config.ApplicationBeansConfiguration;
import com.pszt.housePricingNeuralNetwork.logger.MessageProducer;
import com.pszt.housePricingNeuralNetwork.model.BostonHouse;
import com.pszt.housePricingNeuralNetwork.perceptron.function.IdentityFunction;
import com.pszt.housePricingNeuralNetwork.perceptron.function.SigmoidFunction;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ToString
public class Perceptron {
    private final MessageProducer logger = ApplicationBeansConfiguration.getInstance(MessageProducer.class);
    private final IdentityFunction identityFunction = new IdentityFunction();
    private final SigmoidFunction sigmoidFunction = new SigmoidFunction();

    final float MOMENTUM = 2.7f;
    final float THRESHOLD = 0.03f;
    final float MAX_EPOCH = 1000;
    final float BIAS = 1f;

    private final List<BostonHouse> trainingData;
    private final List<Node> inputLayers;
    private final List<Node> hiddenLayers;
    //        private final List<Node[]> hiddenLayers;
    private final Node output;
    private final float[][] inputToHidden;
    private final List<float[][]> hiddenToHidden;
    private final float[] hiddenToOutput;

    public Perceptron(Integer inputLayerSize, Integer hiddenLayerSize, List<BostonHouse> data) throws IllegalAccessException {
        inputLayerSize += 1;
        hiddenLayerSize += 1;

        this.inputLayers = new ArrayList<>(inputLayerSize);
        this.hiddenLayers = new ArrayList<>(hiddenLayerSize);

        this.inputToHidden = new float[inputLayerSize][hiddenLayerSize];
        this.hiddenToOutput = new float[hiddenLayerSize];
        this.hiddenToHidden = new ArrayList<>(hiddenLayerSize);

        for (int x = 0; x < inputLayerSize; x++) {
            this.inputLayers.add(new Node());
            this.inputLayers.get(x).setFun(identityFunction);
        }
        for (int x = 0; x < hiddenLayerSize; x++) {
            this.hiddenLayers.add(new Node());
            this.hiddenLayers.get(x).setFun(sigmoidFunction);
            this.hiddenToHidden.add(new float[inputLayerSize][inputLayerSize]);
        }

        for (int i = 0; i < inputLayerSize; i++) {
            for (int j = 0; j < hiddenLayerSize; j++) {
                inputToHidden[i][j] = getRandomBetween(-1, 1);
                hiddenToOutput[j] = getRandomBetween(-1, 1);
            }
        }
        logger.trace(Arrays.deepToString(this.inputToHidden));
        logger.trace(Arrays.toString(this.hiddenToOutput));

        this.output = new Node();
        this.output.setFun(sigmoidFunction);

        logger.info("Normalizing training data...");
        this.trainingData = Perceptron.normalizeDataset(data);
        logger.info("Successfully normalized training data");
    }

    private float getRandomBetween(float min, float max) {
        return min + (float) Math.random() * (max - min);
    }

    public void execute() {
        int i = 0;
        float hit, accError;

        do {
            hit = 0;
            accError = 0;

            for (BostonHouse data : trainingData) {
                var values = data.getArrayOfValues();
                values[13] = BIAS;
                feedForward(values);
                backPropagate(data.getMEDV());

                if (this.output.getOutput() / data.getMEDV() > 0.5f) {
                    hit++;
                }

                accError += this.calculateError(data.getMEDV());
//                logger.debug(String.format("%f ?== %f", this.output.getOutput(), data.getMEDV()));
            }

            logger.trace(String.format("%d. precent error: { %f }\tsquare error: { %f }", i, (hit / trainingData.size()), accError));
//            logger.trace(Arrays.deepToString(this.inputToHidden));
//            logger.trace(Arrays.toString(this.hiddenToOutput));
            i++;
        } while (accError >= THRESHOLD && i < MAX_EPOCH);
    }

    public void feedForward(float[] input) throws IllegalArgumentException {
        float[] outputs = new float[this.hiddenLayers.size()];
        float sum = 0;

        for (int y = 0; y < this.hiddenLayers.size(); y++) {
            sum = 0;
            for (int x = 0; x < this.inputLayers.size(); x++) {
                var output = this.inputLayers.get(x).calculateOutput(input[x]);
                sum += output * this.inputToHidden[x][y];
            }
            outputs[y] = this.hiddenLayers.get(y).calculateOutput(sum);
        }

        input = outputs;
        sum = 0;
        for (int y = 0; y < this.hiddenLayers.size(); y++) {
            float output = this.hiddenLayers.get(y).calculateOutput(input[y]);
            sum += output * this.hiddenToOutput[y];
        }

        this.output.calculateOutput(sum);
    }

    public void backPropagate(float expectedOutput) throws IllegalArgumentException {
        /* Obliczanie błędów wstecznych */
        float outputError = expectedOutput - this.output.getOutput();
        this.output.calculateError(outputError);

        float[] hiddenLayersError = new float[hiddenLayers.size()];

        for (int i = 0; i < hiddenLayers.size(); i++) {
            hiddenLayersError[i] = outputError * this.hiddenToOutput[i];
            this.hiddenLayers.get(i).calculateError(hiddenLayersError[i]);
        }

        /* to się przyda dla wielu warstw */
//        float[] inputLayersError = new float[inputLayers.size()];

//        for (int x = 0; x < this.inputLayers.size(); x++) {
//            for (int y = 0; y < this.hiddenLayers.size(); y++) {
//                inputLayersError[x] += this.inputToHidden[x][y] * hiddenLayersError[y];
//            }
//        }

        /* modyfikacja wag */
        for (int y = 0; y < this.hiddenLayers.size(); y++) {
            for (int x = 0; x < this.inputLayers.size(); x++) {
                this.inputToHidden[x][y] += this.MOMENTUM * this.hiddenLayers.get(y).getError() * this.inputLayers.get(x).getInput();
            }
        }

        for (int i = 0; i < hiddenLayers.size(); i++) {
            this.hiddenToOutput[i] += this.MOMENTUM * this.output.getError() * this.hiddenLayers.get(i).getOutput();
        }
    }

    public float calculateError(float expectedValue) {
        var value = this.output.getOutput();
        return (expectedValue - value) * (expectedValue - value) / 2f;
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
