package core;

import dataclasses.SLAEVector;
import exceptions.InvalidDiagonalException;

import java.util.*;

public class SLAESolver {
    private final double DEFAULT_MAX_MATRIX_LINE_VALUE = 0;

    private long iterationsCount = 0;

    private Stack<SLAEVector> errorsStack = new Stack<>();

    private int roundAccuracy;

    public record Solution(SLAEVector solutionVector, long iterationsCount, Stack<SLAEVector> errorsStack) {
        @Override
        public String toString() {
            return String.format("Solution vector: %s\n"
                            + "Iterations count: %s\n"
                            + "Errors list:\n[%s]",
                    solutionVector,
                    iterationsCount,
                    String.join("\n", errorsStack.stream().map(SLAEVector::toString).toArray(String[]::new)));
        }
    }

    public SLAESolver(int roundAccuracy) {
        this.roundAccuracy = roundAccuracy;
    }

    private void clearFields() {
        iterationsCount = 0;
        errorsStack = new Stack<>();
    }

    protected double round(double val) {
        final double multiplier = Math.pow(10, roundAccuracy);
        val *= multiplier;
        val = Math.round(val);
        return val / Math.pow(10, roundAccuracy);
    }

    protected double calcError(SLAEVector newVector, SLAEVector oldVector) {
        double maxError = 0;
        SLAEVector errors = new SLAEVector(new double[newVector.values().length]);
        for (int i = 0; i < newVector.values().length; i++) {
            double error = Math.abs(newVector.values()[i] - oldVector.values()[i]);
            if (error > maxError) maxError = error;
            errors.values()[i] = round(error);
        }
        errorsStack.push(errors);
        return maxError;
    }

    protected SLAEVector calculate(double[][] matrix, double accuracy, SLAEVector lastVector) {
        SLAEVector currentVector = new SLAEVector(new double[matrix.length]);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (j < i) currentVector.values()[i] += matrix[i][j] * lastVector.values()[j];
                if (j == matrix.length - 1) {
                    currentVector.values()[i] += matrix[i][j];
                    continue;
                }
                if (j >= i) currentVector.values()[i] += matrix[i][j] * lastVector.values()[j + 1];
            }
            currentVector.values()[i] = round(currentVector.values()[i]);
        }

        double error = calcError(currentVector, lastVector);

        iterationsCount++;

        if (error < accuracy) return currentVector;
        return calculate(matrix, accuracy, currentVector);
    }

    protected SLAEVector getStartVector(double[][] matrix) {
        double[] dValues = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            dValues[i] = matrix[i][matrix.length - 1];
        }
        return new SLAEVector(dValues);
    }

    //  Выражает иксы по диагоналям
    protected double[][] transformMatrix(double[][] matrix) {
        double[][] resultMatrix = new double[matrix.length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length + 1; j++) {
                if (j == i) continue;
                if (j < i) resultMatrix[i][j] = 0 - matrix[i][j] / matrix[i][i];
                if (j > i) resultMatrix[i][j - 1] = 0 - matrix[i][j] / matrix[i][i];
            }
        }
        return resultMatrix;
    }

    protected List<List<Integer>> getIndexes(double[][] matrix) {
        List<List<Integer>> indexes = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) indexes.add(new LinkedList<>());
        for (int i = 0; i < matrix.length; i++) {
            double max = Arrays.stream(matrix[i]).map(Math::abs).limit(matrix.length).max().orElse(DEFAULT_MAX_MATRIX_LINE_VALUE);
            for (int j = 0; j < matrix.length; j++) {
                if (Math.abs(matrix[i][j]) == max
                        && Arrays.stream(matrix[i]).limit(matrix.length).map(Math::abs).sum() - max <= max)
                    indexes.get(j).add(i); //  Запоминаем строку, подходящую для данного столбца
            }
        }
        return indexes;
    }

    //  Получаем массив индексов-позиций для строк, создающих диагональное преобладание
    protected int[] getMatrixLinesIndexes(double[][] matrix) throws InvalidDiagonalException {
        List<List<Integer>> indexes = getIndexes(matrix); //  Индексы, удовлетворяющие для каждого места в диагонали
        int[] resultIndexes = new int[matrix.length]; //  Отобранные индексы строк для диагонального преобладания
        boolean key = false; //  На случай, если нет ни одного строго удовлетворения неравенство диагонального преобразования, ключ будет указывать на это
        for (int i = 0; i < 2; i++) { //  Две итерации, так как число кандидатов на одну позицию не может превысить 2 -> будут рассмотрены все
            for (int j = 0; j < indexes.size(); j++) { //  Проходимся по всему списку индексов-кандидатов
                if (i == 0 && indexes.get(j).size() == 0)
                    throw new InvalidDiagonalException(); //  Если кандидат на позицию изначально отсутствует -> диагонали нет
                if (indexes.get(j).size() == 1) {
                    key = true;
                    resultIndexes[j] = indexes.get(j).get(0);
                    for (var e : indexes)
                        e.remove(Integer.valueOf(resultIndexes[j])); //  Удаляем отобранный индекс из списка кандидатов
                }
            }
        }
        if (!key) throw new InvalidDiagonalException();
        return resultIndexes;
    }

    protected double[][] transformDiagonal(double[][] matrix) throws InvalidDiagonalException {
        int[] resultIndexes = getMatrixLinesIndexes(matrix);
        double[][] resultMatrix = new double[matrix.length][matrix.length + 1];

        for (int i = 0; i < resultIndexes.length; i++) {
            resultMatrix[i] = matrix[resultIndexes[i]];
        }

        return resultMatrix;
    }

    protected void negLastMatrixColumn(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            matrix[i][matrix.length] *= (-1);
        }
    }

    public Solution solve(double[][] matrix, double accuracy) throws InvalidDiagonalException {
        negLastMatrixColumn(matrix);
        matrix = transformDiagonal(matrix);
        matrix = transformMatrix(matrix);
        SLAEVector startVector = getStartVector(matrix);
        SLAEVector solutionVector = calculate(matrix, accuracy, startVector);
        Solution solution = new Solution(solutionVector, iterationsCount, errorsStack);
        clearFields();
        return solution;
    }

    public void setRoundAccuracy(int roundAccuracy) {
        this.roundAccuracy = roundAccuracy;
    }
}
