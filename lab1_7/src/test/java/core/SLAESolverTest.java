package core;

import dataclasses.SLAEVector;
import exceptions.InvalidDiagonalException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class SLAESolverTest {

    /**
     * Элементарный тест трансформации диагонали валидной матрицы
     */
    @Test
    void transformDiagonalTest1() throws InvalidDiagonalException {
        SLAESolver slaeSolver = new SLAESolver(4);
        double[][] matrix = {
                {11, 4, 3, 2, 1},
                {4, 3, 2, 11, 1},
                {4, 3, 11, 2, 1},
                {4, 11, 3, 2, 1},
                {4, 3, 2, 1, 11}
        };
        double[][] expectedMatrix = {
                {11, 4, 3, 2, 1},
                {4, 11, 3, 2, 1},
                {4, 3, 11, 2, 1},
                {4, 3, 2, 11, 1},
                {4, 3, 2, 1, 11}
        };

        assertArrayEquals(expectedMatrix, slaeSolver.transformDiagonal(matrix));
        System.out.println("Test 1 passed!");
    }

    /**
     * Тестирует случай, когда на каждая строка является кандидатом на 2 позиции, кроме единственного случая
     */
    @Test
    void transformDiagonalTest2() throws InvalidDiagonalException {
        SLAESolver slaeSolver = new SLAESolver(4);
        double[][] matrix = {
                {2,0,2},
                {2,2,0},
                {2,1,0}
        };
        double[][] expectedMatrix = {
                {2,1,0},
                {2,2,0},
                {2,0,2}
        };

        assertArrayEquals(expectedMatrix, slaeSolver.transformDiagonal(matrix));
        System.out.println("Test 2 passed!");
    }


    /**
     * Тестирует случай, когда на место в диагонали есть сразу 4 кандидата
     */
    @Test
    void transformDiagonalTest3() throws InvalidDiagonalException {
        SLAESolver slaeSolver = new SLAESolver(4);
        double[][] matrix = {
                {2,2,0,0},
                {2,0,0,2},
                {2,0,2,0},
                {2,1,0,0}
        };
        double[][] expectedMatrix = {
                {2,1,0,0},
                {2,2,0,0},
                {2,0,2,0},
                {2,0,0,2}
        };

        assertArrayEquals(expectedMatrix, slaeSolver.transformDiagonal(matrix));
        System.out.println("Test 3 passed!");
    }

    /**
     * Тестирует поиск вектора решения для ранее решенного СЛАУ
     */
    @Test
    void calculate() throws InvalidDiagonalException {
        SLAESolver slaeSolver = new SLAESolver(4);
        double[][] matrix = {
                {15, 3, 5, 14},
                {1, 12, 6, 12},
                {9, 3, 50, 13}
        };
        slaeSolver.negLastMatrixColumn(matrix);
        matrix = slaeSolver.transformDiagonal(matrix);
        matrix = slaeSolver.transformMatrix(matrix);
        final double accuracy = 0.01;
        SLAEVector startVector = slaeSolver.getStartVector(matrix);

        SLAEVector slaeVector = new SLAEVector(new double[]{0.7259, 0.8999, 0.0735});

        assertEquals(slaeVector, slaeSolver.calculate(matrix, accuracy, startVector));
        System.out.println("Test 4 passed!");
    }
}