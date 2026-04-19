package app.model;

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
    public String getDisplayValue() {
        return String.valueOf(value);
    }
}