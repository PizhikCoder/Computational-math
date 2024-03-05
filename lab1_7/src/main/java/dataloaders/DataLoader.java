package dataloaders;

import dataclasses.DataContainer;
import exceptions.MatrixLoadingException;

import java.io.BufferedReader;
import java.io.IOException;

public abstract class DataLoader {
    protected double[][] loadMatrix(BufferedReader bufferedReader, final short size) throws IOException, NumberFormatException, MatrixLoadingException {
        double[][] matrix = new double[size][size + 1];
        for (int i = 0; i < size; i++){
            String[] numStrings = bufferedReader.readLine().replaceAll("\\s+", " ").split(" ");
            if (numStrings.length != size + 1) throw new MatrixLoadingException();
            for (int j = 0; j < size + 1; j++){
                matrix[i][j] = Double.parseDouble(numStrings[j].replace(",", "."));
            }
        }
        return matrix;
    }
    public abstract DataContainer load() throws IOException, NumberFormatException, MatrixLoadingException;
}
