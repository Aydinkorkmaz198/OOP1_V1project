package app.model;

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
    public String getDisplayValue() {
        return String.valueOf(value);
    }
}