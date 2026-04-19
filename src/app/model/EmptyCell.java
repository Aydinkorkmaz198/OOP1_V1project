package app.model;

public class EmptyCell extends Cell {

    public EmptyCell() {
        super("");
    }

    @Override
    public String getDisplayValue() {
        return "";
    }
}