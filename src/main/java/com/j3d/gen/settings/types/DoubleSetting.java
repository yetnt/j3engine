package com.j3d.gen.settings.types;

import com.j3d.gen.settings.Setting;
import com.j3d.ui.settings.panels.NumberValueSPanel;

import java.util.function.Function;

public class DoubleSetting extends Setting<Double> {
    private final double min;
    private final double max;

    private Function<Double, Integer> toInt;
    private Function<Integer, Double> toDouble;
    private double stepSize;

    public DoubleSetting(String name, Double value, String description, double min, double max) {
        super(name, value, description);
        this.min = min;
        this.max = max;
    }

    public DoubleSetting setValues(Function<Double, Integer> toInt, Function<Integer, Double> toDouble, double stepSize) {
        this.toInt = toInt;
        this.toDouble = toDouble;
        this.stepSize = stepSize;
        return this;
    }

    @Override
    public Double fromString(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return getDefaultValue();
        }
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    @Override
    public void setValue(Double value) {
        super.setValue(
                Math.max(min, Math.min(max, value))
        );
    }

    @Override
    public NumberValueSPanel<Double> panel() {
        return new NumberValueSPanel<>(this, min, max, stepSize, toInt, toDouble);
    }

    @Override
    public DoubleSetting onSetValue(Function<Double, Void> callback) {
        return (DoubleSetting) super.onSetValue(callback);
    }
}
