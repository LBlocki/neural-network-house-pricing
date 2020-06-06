package com.pszt.housePricingNeuralNetwork.model;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public class BostonHouse {

    @CsvBindByPosition(position = 0)
    private Float CRIM;

    @CsvBindByPosition(position = 1)
    private Float ZN;

    @CsvBindByPosition(position = 2)
    private Float INDUS;

    @CsvBindByPosition(position = 3)
    private Float CHAS;

    @CsvBindByPosition(position = 4)
    private Float NOX;

    @CsvBindByPosition(position = 5)
    private Float RM;

    @CsvBindByPosition(position = 6)
    private Float AGE;

    @CsvBindByPosition(position = 7)
    private Float DIS;

    @CsvBindByPosition(position = 8)
    private Float RAD;

    @CsvBindByPosition(position = 9)
    private Float TAX;

    @CsvBindByPosition(position = 10)
    private Float PTRATIO;

    @CsvBindByPosition(position = 11)
    private Float B;

    @CsvBindByPosition(position = 12)
    private Float LSTAT;

    @CsvBindByPosition(position = 13)
    private Float MEDV = null;

    public List<Float> getListOfValues() {
        return Arrays.asList(CRIM, ZN, INDUS, CHAS, NOX, RM, AGE, DIS, RAD, TAX, B, LSTAT, PTRATIO, MEDV);
    }

    // normalize single data set to range [0, 1]
    public static void normalize(BostonHouse data, Float min, Float max) throws IllegalAccessException {
        for (Field f : BostonHouse.class.getDeclaredFields()) {
            var normalized = ((float) f.get(data) - min) / (max - min);
            f.set(data, normalized);
        }
    }
}
