package com.bedatadriven.spss;

public class MissingValue {
    private final double min;

    private final double max;

    public MissingValue(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getValue() {
        return min;
    }

    public boolean isRange() {
        return min != max;
    }
}
