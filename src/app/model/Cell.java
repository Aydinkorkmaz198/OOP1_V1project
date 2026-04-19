package app.model;

public abstract class Cell {
    protected String rawContent;

    public Cell(String rawContent) {
        this.rawContent = rawContent;
    }

    public String getRawContent() {
        return rawContent;
    }

    // Returns the text that will be shown on screen
    public abstract String getDisplayValue();
}