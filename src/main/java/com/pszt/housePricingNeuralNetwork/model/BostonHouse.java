package com.pszt.housePricingNeuralNetwork.model;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

@Data
public class BostonHouse {

    @CsvBindByPosition(position = 0)
    private float CRIM;

    @CsvBindByPosition(position = 1)
    private float ZN;

    @CsvBindByPosition(position = 2)
    private float INDUS;

    @CsvBindByPosition(position = 3)
    private float CHAS;

    @CsvBindByPosition(position = 4)
    private float NOX;

    @CsvBindByPosition(position = 5)
    private float RM;

    @CsvBindByPosition(position = 6)
    private float AGE;

    @CsvBindByPosition(position = 7)
    private float DIS;

    @CsvBindByPosition(position = 8)
    private float RAD;

    @CsvBindByPosition(position = 9)
    private float TAX;

    @CsvBindByPosition(position = 10)
    private float PTRATIO;

    @CsvBindByPosition(position = 11)
    private float B;

    @CsvBindByPosition(position = 12)
    private float LSTAT;

    @CsvBindByPosition(position = 13)
    private float MEDV = 0;
}
