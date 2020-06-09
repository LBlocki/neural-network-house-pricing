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

    final float MOMENTUM = 1.7f;
    final float THRESHOLD = 0.005f;
    final float MAX_EPOCH = 1000;
    final float BIAS = 1f;

    private final List<BostonHouse> trainingData;
    private final List<Node> inputLayers;
    private final List<Node[]> hiddenLayers;
    private final Node output;
    private final float[][] inputToHidden;
    private final List<float[][]> hiddenToHidden;
    private final float[] hiddenToOutput;

    public Perceptron(Integer inputLayerSize, Integer hiddenLayerSize, Integer hiddenLayersCount, List<BostonHouse> data) throws IllegalAccessException {
        // dla BIASu
        inputLayerSize += 1;
        hiddenLayerSize += 1;

        this.inputLayers = new ArrayList<>(inputLayerSize);
        this.hiddenLayers = new ArrayList<>(hiddenLayerSize);
        this.inputToHidden = new float[inputLayerSize][hiddenLayerSize];
        this.hiddenToOutput = new float[hiddenLayerSize];
        this.hiddenToHidden = new ArrayList<>(hiddenLayersCount - 1);

        for (int x = 0; x < inputLayerSize; x++) {
            this.inputLayers.add(new Node());
            this.inputLayers.get(x).setFun(identityFunction);
        }

        for (int x = 0; x < hiddenLayersCount; x++) {
            Node[] nodes = new Node[hiddenLayerSize];
            for (int y = 0; y < hiddenLayerSize; y++) {
                nodes[y] = new Node();
                nodes[y].setFun(sigmoidFunction);
            }
            this.hiddenLayers.add(nodes);
        }

        for (int i = 0; i < inputLayerSize; i++) {
            for (int j = 0; j < hiddenLayerSize; j++) {
                inputToHidden[i][j] = getRandomBetween(-1, 1);
                hiddenToOutput[j] = getRandomBetween(-1, 1);
            }
        }
        logger.trace(String.format("{ inputToHidden } %s", Arrays.deepToString(inputToHidden)));
        logger.trace(String.format("{ hiddenToOutput } %s", Arrays.toString(hiddenToOutput)));


        for (int x = 0; x < hiddenLayersCount - 1; x++) {
            float[][] layerCons = new float[hiddenLayerSize][hiddenLayerSize];

            for (int i = 0; i < hiddenLayerSize; i++) {
                for (int j = 0; j < hiddenLayerSize; j++) {
                    layerCons[i][j] = getRandomBetween(-1, 1);
                }
            }
            this.hiddenToHidden.add(layerCons);
            logger.trace(String.format("{ %d } %s", x, Arrays.deepToString(layerCons)));
        }

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
        float accError;

        do {
            accError = 0;

            for (BostonHouse data : trainingData) {
                var values = data.getArrayOfValues();
                values[13] = BIAS;
                feedForward(values);
                backPropagate(data.getMEDV());
                accError += this.calculateError(data.getMEDV());
            }

            logger.trace(String.format("%d. square error: { %f }", i, accError));
            i++;
        } while (accError >= THRESHOLD && i < MAX_EPOCH);
    }

    public void feedForward(float[] input) throws IllegalArgumentException {

        /* input layer connected to first hidden layer */
        // iterujemy po nodach na pierwszej warstwie ukrytej
        var firstHiddenLayer = this.hiddenLayers.get(0);
        float[] outputs = new float[firstHiddenLayer.length];
        float sum;
        for (int x = 0; x < firstHiddenLayer.length; x++) {
            sum = 0;
            // iterujemy po danych wejściowych x1, x2 .. xn
            // liczymy sume x1 * waga krawędzi inputToHidden
            for (int y = 0; y < input.length; y++) {
                this.inputLayers.get(y).calculateOutput(input[y]);
                sum += input[y] * this.inputToHidden[y][x];
            }
            // liczymy output dla noda z pierwszej warstwy ukrytej
            outputs[x] = firstHiddenLayer[x].calculateOutput(sum);
        }

        /* hidden layers connected to next hidden layer*/
        input = outputs;
        // iterujemy po warstwach ukrytych
        for (int i = 0; i < this.hiddenToHidden.size(); i++) {
            Node[] currLayer = this.hiddenLayers.get(i);
            Node[] nextLayer = this.hiddenLayers.get(i + 1);

            for (int x = 0; x < nextLayer.length; x++) {
                sum = 0;
                for (int y = 0; y < currLayer.length; y++) {
                    sum += input[y] * this.hiddenToHidden.get(i)[y][x];
                }
                outputs[x] = nextLayer[x].calculateOutput(sum);
            }
            input = outputs;
        }

        /* last hidden layer connected with output*/
        input = outputs;
        sum = 0;
        for (int y = 0; y < this.hiddenToOutput.length; y++) {
            sum += input[y] * this.hiddenToOutput[y];
        }
        this.output.calculateOutput(sum);

    }

    public void backPropagate(float expectedOutput) throws IllegalArgumentException {
        /* Obliczanie błędów wstecznych */
        float outputError = expectedOutput - this.output.getOutput();
        this.output.calculateError(outputError);

        float[] hiddenLayersError = new float[hiddenToOutput.length];

        // output to last hidden
        for (int i = 0; i < hiddenToOutput.length; i++) {
            hiddenLayersError[i] = outputError * this.hiddenToOutput[i];
            this.hiddenLayers.get(this.hiddenLayers.size() - 1)[i].calculateError(hiddenLayersError[i]);
        }

        // hidden to previous hidden
        for (int i = this.hiddenToHidden.size() - 1; i > 0; i--) {
            Node[] prevLayer = this.hiddenLayers.get(i - 1);
            Node[] currLayer = this.hiddenLayers.get(i);
            for (int x = 0; x < prevLayer.length; x++) {
                float[] layerError = new float[prevLayer.length];
                for (int y = 0; y < currLayer.length; y++) {
                    layerError[x] += this.hiddenToHidden.get(i)[x][y] * hiddenLayersError[y];
                }
                hiddenLayersError = layerError;
                prevLayer[x].calculateError(hiddenLayersError[x]);
            }
        }


        /* modyfikacja wag */
        // input to first hidden
        for (int y = 0; y < this.hiddenLayers.get(0).length; y++) {
            for (int x = 0; x < this.inputLayers.size(); x++) {
                this.inputToHidden[x][y] +=
                        this.MOMENTUM * this.hiddenLayers.get(0)[y].getError() * this.inputLayers.get(x).getInput();
            }
        }

        // hidden to next hidden
        for (int i = 0; i < this.hiddenToHidden.size(); i++) {
            Node[] currLayer = this.hiddenLayers.get(i);
            Node[] nextLayer = this.hiddenLayers.get(i + 1);
            for (int x = 0; x < currLayer.length; x++) {
                for (int y = 0; y < nextLayer.length; y++) {
                    this.hiddenToHidden.get(i)[x][y] += this.MOMENTUM * nextLayer[y].getError() * currLayer[x].getOutput();
                }
            }
        }

        // last hidden to output
        for (int i = 0; i < this.hiddenToOutput.length; i++) {
            this.hiddenToOutput[i] +=
                    this.MOMENTUM * this.output.getError() * this.hiddenLayers.get(this.hiddenLayers.size() - 1)[i].getOutput();
        }
    }

    public float calculateError(float expectedValue) {
        var value = this.output.getOutput();
        return (expectedValue - value) * (expectedValue - value) / this.inputLayers.size();
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
