package app.factory;

import app.exception.InvalidCellException;
import app.model.Cell;
import app.model.DoubleCell;
import app.model.EmptyCell;
import app.model.FormulaCell;
import app.model.IntegerCell;
import app.model.StringCell;

public class CellFactory {

    public static Cell createCell(String text) throws InvalidCellException {
        String trimmed = text.trim();

        // Empty cell
        if (trimmed.isEmpty()) {
            return new EmptyCell();
        }

        // Formula cell
        if (trimmed.startsWith("=")) {
            return new FormulaCell(trimmed);
        }

        // String cell
        if (isQuotedString(trimmed)) {
            String parsed = parseString(trimmed);
            return new StringCell(trimmed, parsed);
        }

        // Integer cell
        if (trimmed.matches("[+-]?\\d+")) {
            try {
                return new IntegerCell(trimmed, Integer.parseInt(trimmed));
            } catch (NumberFormatException e) {
                throw new InvalidCellException("Invalid integer: " + trimmed);
            }
        }

        // Fractional number cell
        if (trimmed.matches("[+-]?\\d+\\.\\d+")) {
            try {
                return new DoubleCell(trimmed, Double.parseDouble(trimmed));
            } catch (NumberFormatException e) {
                throw new InvalidCellException("Invalid fractional number: " + trimmed);
            }
        }

        throw new InvalidCellException("Unknown cell type: " + trimmed);
    }

    private static boolean isQuotedString(String text) {
        return text.length() >= 2 && text.startsWith("\"") && text.endsWith("\"");
    }

    private static String parseString(String text) {
        // Remove outer quotes
        String inner = text.substring(1, text.length() - 1);

        // Support escaped quote and backslash
        inner = inner.replace("\\\"", "\"");
        inner = inner.replace("\\\\", "\\");

        return inner;
    }
}