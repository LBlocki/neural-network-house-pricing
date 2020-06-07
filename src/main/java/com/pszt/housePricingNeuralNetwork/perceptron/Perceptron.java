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
    private final List<Node[]> hiddenLayers;
    private final Node output;
    private final List<float[][]> connections;

    public Perceptron(Integer inputNodeCount, Integer hiddenLayerSize, List<BostonHouse> data) throws IllegalAccessException {
        inputNodeCount += 1;

        this.inputLayers = new ArrayList<>(inputNodeCount);
        this.hiddenLayers = new ArrayList<>(hiddenLayerSize);
        this.connections = new ArrayList<>(hiddenLayerSize + 1);

        for (int x = 0; x < inputNodeCount; x++) {
            this.inputLayers.add(new Node());
            this.inputLayers.get(x).setFun(identityFunction);
        }

        for (int x = 0; x < hiddenLayerSize; x++) {
            Node[] nodes = new Node[inputNodeCount];
            for (int y = 0; y < inputNodeCount; y++) {
                nodes[y] = new Node();
                nodes[y].setFun(sigmoidFunction);
            }
            this.hiddenLayers.add(nodes);
        }

        for (int x = 0; x < hiddenLayerSize + 1; x++) {
            float[][] layerCons = new float[inputNodeCount][inputNodeCount];

            for (int i = 0; i < inputNodeCount; i++) {
                if (i == inputNodeCount - 1) {
                    // hidden to output layer
                    layerCons[i][0] = getRandomBetween(-1, 1);
                } else {
                    for (int j = 0; j < inputNodeCount; j++) {
                        layerCons[i][j] = getRandomBetween(-1, 1);
                    }
                }
            }
            this.connections.add(layerCons);
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
            }

            logger.trace(String.format("%d. precent error: { %f }\tsquare error: { %f }", i, (hit / trainingData.size()), accError));
            i++;
        } while (accError >= THRESHOLD && i < MAX_EPOCH);
    }

    public void feedForward(float[] input) throws IllegalArgumentException {

        float[] outputs = new float[this.inputLayers.size()];
        float sum;

        /* input layer connected  */
        // iterujemy po nodach na pierwszej warstwie ukrytej
        var firstHiddenLayer = this.hiddenLayers.get(0);
        for (int x = 0; x < firstHiddenLayer.length; x++) {
            sum = 0;
            // iterujemy po danych wejściowych x1, x2 .. xn
            // liczymy sume x1 * waga krawędzi input to hidden ( this.connections.get(0) )
            for (int y = 0; y < input.length; y++) {
                this.inputLayers.get(y).calculateOutput(input[y]);
                sum += input[y] * this.connections.get(0)[y][x];
            }
            // liczymy output dla noda z pierwszej warstwy ukrytej
            outputs[x] = firstHiddenLayer[x].calculateOutput(sum);
        }

        /* hidden layers */
        input = outputs;
        // iterujemy po warstwach ukrytych (od 2 do przedostatniej)
        for (int i = 1; i < this.connections.size() - 1; i++) {
            Node[] currLayer = this.hiddenLayers.get(i - 1);
            Node[] nextLayer = this.hiddenLayers.get(i);
            for (int x = 0; x < nextLayer.length; x++) {
                sum = 0;
                for (int y = 0; y < currLayer.length; y++) {
                    sum += input[y] * this.connections.get(i)[y][x];
                }
                outputs[x] = nextLayer[x].calculateOutput(sum);
            }
            input = outputs;
        }

        /* last hidden layer connected with output*/
        input = outputs;
        sum = 0;
        for (int y = 0; y < this.inputLayers.size(); y++) {
            sum += input[y] * this.connections.get(this.connections.size() - 1)[y][0];
        }
        this.output.calculateOutput(sum);

    }

    public void backPropagate(float expectedOutput) throws IllegalArgumentException {
        /* Obliczanie błędów wstecznych */
        float outputError = expectedOutput - this.output.getOutput();
        this.output.calculateError(outputError);

        float[] hiddenLayersError = new float[inputLayers.size()];

        // output to last hidden
        for (int i = 0; i < inputLayers.size(); i++) {
            hiddenLayersError[i] = outputError * this.connections.get(this.connections.size() - 1)[i][0];
            this.hiddenLayers.get(this.hiddenLayers.size() - 1)[i].calculateError(hiddenLayersError[i]);
        }

        // hidden to previous hidden
        for (int i = this.connections.size() - 2; i > 0; i--) {
            Node[] layer = this.hiddenLayers.get(i - 1);
            for (int x = 0; x < layer.length; x++) {
                float[] layerError = new float[inputLayers.size()];
                for (int y = 0; y < this.inputLayers.size(); y++) {
                    layerError[x] += this.connections.get(i)[x][y] * hiddenLayersError[y];
                }
                hiddenLayersError = layerError;
                layer[x].calculateError(hiddenLayersError[x]);
            }
        }


        /* modyfikacja wag */
        // input to first hidden
        for (int y = 0; y < this.inputLayers.size(); y++) {
            for (int x = 0; x < this.inputLayers.size(); x++) {
                this.connections.get(0)[x][y] +=
                        this.MOMENTUM * this.hiddenLayers.get(0)[y].getError() * this.inputLayers.get(x).getInput();
            }
        }

        // hidden to next hidden
        for (int i = 1; i < this.connections.size() - 1; i++) {
            Node[] currLayer = this.hiddenLayers.get(i - 1);
            Node[] nextLayer = this.hiddenLayers.get(i);
            for (int x = 0; x < currLayer.length; x++) {
                for (int y = 0; y < nextLayer.length; y++) {
                    this.connections.get(i)[x][y] += this.MOMENTUM * nextLayer[y].getError() * currLayer[x].getOutput();
                }
            }
        }

        // last hidden to output
        for (int i = 0; i < this.inputLayers.size(); i++) {
            this.connections.get(this.connections.size() - 1)[i][0] +=
                    this.MOMENTUM * this.output.getError() * this.hiddenLayers.get(this.hiddenLayers.size() - 1)[i].getOutput();
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
