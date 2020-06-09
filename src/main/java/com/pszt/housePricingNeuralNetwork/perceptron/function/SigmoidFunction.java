package com.pszt.housePricingNeuralNetwork.perceptron.function;

public class SigmoidFunction implements Function {

    @Override
    public float activate(float input) {
        return (float) (1f / (1f + Math.exp(-input)));
    }

    @Override
    public float derivate(float input) {
        return input * (1f - input);
    }
}
