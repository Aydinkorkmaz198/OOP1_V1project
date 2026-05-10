package app.factory;

import app.exception.InvalidCellException;
import app.model.*;

public class CellFactory {

    public static Cell createCell(String text) throws InvalidCellException {
        String trimmed = text.trim();

        if (trimmed.isEmpty()) {
            return new EmptyCell();
        }

        if (trimmed.startsWith("=")) {
            return new FormulaCell(trimmed);
        }

        if (isQuotedString(trimmed)) {
            return new StringCell(trimmed, parseString(trimmed));
        }

        if (trimmed.matches("[+-]?\\d+")) {
            return new IntegerCell(trimmed, Integer.parseInt(trimmed));
        }

        if (trimmed.matches("[+-]?\\d+\\.\\d+")) {
            return new DoubleCell(trimmed, Double.parseDouble(trimmed));
        }

        throw new InvalidCellException("Unknown cell type: " + trimmed);
    }

    private static boolean isQuotedString(String text) throws InvalidCellException {
        if (text.startsWith("\"") && text.endsWith("\"") && text.length() >= 2) {
            return true;
        }

        if (text.startsWith("\"") || text.endsWith("\"")) {
            throw new InvalidCellException("Invalid string format: " + text);
        }

        return false;
    }

    private static String parseString(String text) {
        String inner = text.substring(1, text.length() - 1);

        inner = inner.replace("\\\"", "\"");
        inner = inner.replace("\\\\", "\\");

        return inner;
    }
}