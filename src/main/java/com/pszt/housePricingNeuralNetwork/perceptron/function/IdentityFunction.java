package com.pszt.housePricingNeuralNetwork.perceptron.function;

public class IdentityFunction implements Function {

    @Override
    public float activate(float input) {
        return input;
    }

    @Override
    public float derivate(float input) {
        return 1f;
    }

}
