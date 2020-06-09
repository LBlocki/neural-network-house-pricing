package com.pszt.housePricingNeuralNetwork.perceptron;

import com.pszt.housePricingNeuralNetwork.perceptron.function.Function;
import lombok.Data;

@Data
public class Node {
    // This node's activation function; may be a continuous function.
    private Function fun;
    // The last input through feed forwarding into this node.
    private float input = 0f;
    // The last output from this node.
    private float output = 0f;
    // The last error calculated from the last output.
    private float error = 0f;

    public float calculateOutput(float input) {
        this.input = input;
        this.output = this.fun.activate(input);
        return output;
    }

    public float calculateError(float err) {
        this.error = fun.derivate(this.output) * err;
        return error;
    }

}
