package app.model;

import app.core.Spreadsheet;
import app.exception.EvaluationException;

public class StringCell extends Cell {
    private String value;

    public StringCell(String rawContent, String value) {
        super(rawContent);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getDisplayValue(Spreadsheet sheet) {
        return value;
    }

    @Override
    public double getNumericValue(Spreadsheet sheet) throws EvaluationException {
        // Convert string to number only if it matches valid numeric forms
        if (value.matches("[+-]?\\d+")) {
            return Integer.parseInt(value);
        }

        if (value.matches("[+-]?\\d+\\.\\d+")) {
            return Double.parseDouble(value);
        }

        return 0;
    }
}