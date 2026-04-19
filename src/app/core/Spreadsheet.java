package app.core;

import app.exception.InvalidCellException;
import app.factory.CellFactory;
import app.model.Cell;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Basic spreadsheet structure for the first project stage
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

        // Keep trailing empty cells
        String[] parts = line.split(",", -1);

        for (int col = 0; col < parts.length; col++) {
            String token = parts[col];

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

    public void printTable() {
        int maxColumns = getMaxColumnCount();
        int[] columnWidths = getColumnWidths(maxColumns);

        for (List<Cell> row : rows) {
            for (int col = 0; col < maxColumns; col++) {
                String value = "";

                if (col < row.size()) {
                    value = row.get(col).getDisplayValue();
                }

                System.out.printf("%-" + columnWidths[col] + "s", value);

                if (col < maxColumns - 1) {
                    System.out.print(" | ");
                }
            }
            System.out.println();
        }
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
            for (int col = 0; col < row.size(); col++) {
                String value = row.get(col).getDisplayValue();
                if (value.length() > widths[col]) {
                    widths[col] = value.length();
                }
            }
        }

        // Prevent zero width columns
        for (int i = 0; i < widths.length; i++) {
            if (widths[i] == 0) {
                widths[i] = 1;
            }
        }

        return widths;
    }
}