package dataclasses;

import java.util.Arrays;

public record SLAEVector(double[] values) {
    @Override
    public String toString() {
        String vector = "(";
        for (int i = 0; i < values.length; i++) {
            vector += String.valueOf(values[i]);
            if (i != values.length - 1) vector += "; ";
        }
        return vector + ")";
    }
}
