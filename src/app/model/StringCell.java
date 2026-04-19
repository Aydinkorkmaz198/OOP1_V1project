package app.model;

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
    public String getDisplayValue() {
        return value;
    }
}