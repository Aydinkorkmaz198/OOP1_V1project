package app.model;

import app.core.Spreadsheet;
import app.exception.EvaluationException;

public class EmptyCell extends Cell {

    public EmptyCell() {
        super("");
    }

    @Override
    public String getDisplayValue(Spreadsheet sheet) {
        return "";
    }

    @Override
    public double getNumericValue(Spreadsheet sheet) throws EvaluationException {
        return 0;
    }
}