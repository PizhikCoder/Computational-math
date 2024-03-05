package dataloaders;

import dataclasses.DataContainer;
import exceptions.MatrixLoadingException;

import java.io.*;

public class FileDataLoader extends DataLoader {
    private String filePath;

    public FileDataLoader(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public DataContainer load() throws IOException, NumberFormatException, MatrixLoadingException {
        short size;
        double accuracy;
        double[][] matrix;
        File file = new File(filePath);
        try(FileReader fileReader = new FileReader(file)){
            try(BufferedReader bufferedReader = new BufferedReader(fileReader)){
                String sizeString = bufferedReader.readLine();
                size = Short.parseShort(sizeString);
                String accuracyString = bufferedReader.readLine();
                accuracy = Double.parseDouble(accuracyString);
                matrix = loadMatrix(bufferedReader, size);
            }
        }
        return new DataContainer(size, accuracy, matrix);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
