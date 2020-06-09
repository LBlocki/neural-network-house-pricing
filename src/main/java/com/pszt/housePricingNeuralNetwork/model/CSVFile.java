package com.pszt.housePricingNeuralNetwork.model;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.Data;
import lombok.Value;

import java.io.File;
import java.io.FileReader;
import java.util.List;

@Data
@Value
public class CSVFile {
    File file;
    List<BostonHouse> data;
}
