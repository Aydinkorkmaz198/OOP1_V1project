package app;

import app.core.Spreadsheet;
import app.exception.InvalidCellException;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Spreadsheet sheet = new Spreadsheet();
        Scanner scanner = new Scanner(System.in);

        String fileName = "input.txt";

        try {
            sheet.loadFromFile(fileName);
            System.out.println("Table loaded successfully.");
            System.out.println("Type 'help' to see available commands.\n");
        } catch (InvalidCellException e) {
            System.out.println("Input error: " + e.getMessage());
            return;
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
            return;
        }

        boolean running = true;

        while (running) {
            System.out.print("> ");
            String commandLine = scanner.nextLine().trim();

            if (commandLine.equalsIgnoreCase("print")) {
                sheet.printTable();

            } else if (commandLine.equalsIgnoreCase("help")) {
                printHelp();

            } else if (commandLine.equalsIgnoreCase("exit")) {
                running = false;

            } else if (commandLine.startsWith("edit")) {
                handleEditCommand(sheet, commandLine);

            } else if (commandLine.isEmpty()) {
                // Ignore empty command

            } else {
                System.out.println("Unknown command. Type 'help' for available commands.");
            }
        }

        System.out.println("Program closed.");
    }

    private static void handleEditCommand(Spreadsheet sheet, String commandLine) {
        // Expected format: edit row column value
        String[] parts = commandLine.split("\\s+", 4);

        if (parts.length < 4) {
            System.out.println("Invalid edit command. Example: edit 2 3 100");
            return;
        }

        try {
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);
            String newValue = parts[3];

            sheet.editCell(row, col, newValue);
            System.out.println("Cell updated successfully.");

        } catch (NumberFormatException e) {
            System.out.println("Row and column must be valid numbers.");

        } catch (InvalidCellException e) {
            System.out.println("Edit error: " + e.getMessage());
        }
    }

    private static void printHelp() {
        System.out.println("Available commands:");
        System.out.println("print                  - Displays the table");
        System.out.println("edit row col value     - Changes a cell value");
        System.out.println("help                   - Shows available commands");
        System.out.println("exit                   - Closes the program");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("edit 1 2 100");
        System.out.println("edit 2 3 \"Hello\"");
        System.out.println("edit 3 1 =R1C1+R1C2");
    }
}