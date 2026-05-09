package app.core;

import app.exception.InvalidCellException;
import app.factory.CellFactory;
import app.model.Cell;
import app.model.EmptyCell;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Main table structure for the spreadsheet
public class Spreadsheet {
    private List<List<Cell>> rows;

    public Spreadsheet() {
        rows = new ArrayList<>();
    }

    public void loadFromFile(String fileName) throws IOException, InvalidCellException {
        rows.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int rowNumber = 0;

            while ((line = reader.readLine()) != null) {
                rowNumber++;
                List<Cell> row = parseLine(line, rowNumber);
                rows.add(row);
            }
        }
    }

    private List<Cell> parseLine(String line, int rowNumber) throws InvalidCellException {
        List<Cell> row = new ArrayList<>();

        // Splits line by commas, but ignores commas inside quoted strings
        List<String> parts = splitLineRespectingQuotes(line);

        for (int col = 0; col < parts.size(); col++) {
            String token = parts.get(col);

            try {
                Cell cell = CellFactory.createCell(token);
                row.add(cell);
            } catch (InvalidCellException e) {
                throw new InvalidCellException(
                        "Error at row " + rowNumber + ", col " + (col + 1) + ": " + token.trim()
                );
            }
        }

        return row;
    }

    private List<String> splitLineRespectingQuotes(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        boolean insideQuotes = false;
        boolean escaped = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (escaped) {
                current.append(ch);
                escaped = false;
                continue;
            }

            if (ch == '\\') {
                current.append(ch);
                escaped = true;
                continue;
            }

            if (ch == '"') {
                insideQuotes = !insideQuotes;
                current.append(ch);
                continue;
            }

            if (ch == ',' && !insideQuotes) {
                parts.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        parts.add(current.toString());
        return parts;
    }

    public void saveToFile(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0; i < rows.size(); i++) {
                List<Cell> row = rows.get(i);

                for (int j = 0; j < row.size(); j++) {
                    writer.write(row.get(j).getRawContent());

                    if (j < row.size() - 1) {
                        writer.write(",");
                    }
                }

                if (i < rows.size() - 1) {
                    writer.newLine();
                }
            }
        }
    }

    public void clear() {
        rows.clear();
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

    public void printTable() {
        int maxColumns = getMaxColumnCount();

        if (maxColumns == 0) {
            System.out.println("Table is empty.");
            return;
        }

        int[] columnWidths = getColumnWidths(maxColumns);

        for (List<Cell> row : rows) {
            for (int col = 0; col < maxColumns; col++) {
                String value = "";

                if (col < row.size()) {
                    value = row.get(col).getDisplayValue(this);
                }

                System.out.printf("%-" + columnWidths[col] + "s", value);

                if (col < maxColumns - 1) {
                    System.out.print(" | ");
                }
            }

            System.out.println();
        }
    }

    public void editCell(int rowNumber, int colNumber, String newValue) throws InvalidCellException {
        if (rowNumber < 1 || colNumber < 1) {
            throw new InvalidCellException("Row and column numbers must start from 1.");
        }

        // Validate first. If invalid, the table is not changed.
        Cell newCell = CellFactory.createCell(newValue);

        // Add missing rows
        while (rows.size() < rowNumber) {
            rows.add(new ArrayList<>());
        }

        List<Cell> targetRow = rows.get(rowNumber - 1);

        // Add missing columns as empty cells
        while (targetRow.size() < colNumber) {
            targetRow.add(new EmptyCell());
        }

        targetRow.set(colNumber - 1, newCell);
    }

    public Cell getCell(int row, int col) {
        int rowIndex = row - 1;
        int colIndex = col - 1;

        // Out-of-range references are treated as empty cells
        if (rowIndex < 0 || rowIndex >= rows.size()) {
            return new EmptyCell();
        }

        List<Cell> targetRow = rows.get(rowIndex);

        if (colIndex < 0 || colIndex >= targetRow.size()) {
            return new EmptyCell();
        }

        return targetRow.get(colIndex);
    }

    public String getCellRawContent(int row, int col) {
        Cell cell = getCell(row, col);
        return cell.getRawContent();
    }

    private int getMaxColumnCount() {
        int max = 0;

        for (List<Cell> row : rows) {
            if (row.size() > max) {
                max = row.size();
            }
        }

        return max;
    }

    private int[] getColumnWidths(int maxColumns) {
        int[] widths = new int[maxColumns];

        for (List<Cell> row : rows) {
            for (int col = 0; col < maxColumns; col++) {
                String value = "";

                if (col < row.size()) {
                    value = row.get(col).getDisplayValue(this);
                }

                if (value.length() > widths[col]) {
                    widths[col] = value.length();
                }
            }
        }

        // Prevent zero-width columns
        for (int i = 0; i < widths.length; i++) {
            if (widths[i] == 0) {
                widths[i] = 1;
            }
        }

        return widths;
    }
}