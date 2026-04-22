package app.model;

import app.core.Spreadsheet;
import app.exception.EvaluationException;

public class DoubleCell extends Cell {
    private double value;

    public DoubleCell(String rawContent, double value) {
        super(rawContent);
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String getDisplayValue(Spreadsheet sheet) {
        return String.valueOf(value);
    }

    @Override
    public double getNumericValue(Spreadsheet sheet) throws EvaluationException {
        return value;
    }
}