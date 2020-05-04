package com.pszt.housePricingNeuralNetwork.execute;

public interface ExecutionService {

    void addObserver(ExecutionObserver observer);

    boolean canRunExecution();

    void execute();

    interface ExecutionObserver {

        void reactToExecutionStart();

        void reactToExecutionEnd();

        boolean isAbortRequested();
    }
}
