package app.model;

import app.core.Spreadsheet;
import app.exception.EvaluationException;

public class IntegerCell extends Cell {
    private int value;

    public IntegerCell(String rawContent, int value) {
        super(rawContent);
        this.value = value;
    }

    public int getValue() {
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