package dataloaders;

import dataclasses.DataContainer;
import exceptions.MatrixLoadingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleDataLoader extends DataLoader {
    @Override
    public DataContainer load() throws IOException, NumberFormatException, MatrixLoadingException {
        short size;
        double accuracy;
        double[][] matrix;
        try (InputStreamReader inputStreamReader = new InputStreamReader(System.in)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                System.out.print("Enter the number of lines:\n> ");
                String sizeString = bufferedReader.readLine();
                size = Short.parseShort(sizeString);
                System.out.print("Enter accuracy:\n> ");
                String accuracyString = bufferedReader.readLine();
                accuracy = Double.parseDouble(accuracyString);
                System.out.print("Enter the matrix lines:\n> ");
                matrix = loadMatrix(bufferedReader, size);
            }
        }
        return new DataContainer(size, accuracy, matrix);
    }
}
