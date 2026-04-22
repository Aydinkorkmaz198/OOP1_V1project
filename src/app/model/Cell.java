package app.model;

import app.core.Spreadsheet;
import app.exception.EvaluationException;

public abstract class Cell {
    protected String rawContent;

    public Cell(String rawContent) {
        this.rawContent = rawContent;
    }

    public String getRawContent() {
        return rawContent;
    }

    // Returns the text shown in the table
    public abstract String getDisplayValue(Spreadsheet sheet);

    // Returns numeric value for formulas
    public abstract double getNumericValue(Spreadsheet sheet) throws EvaluationException;
}