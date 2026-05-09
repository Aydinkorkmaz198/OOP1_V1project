package app;

import app.core.Spreadsheet;
import app.exception.InvalidCellException;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Spreadsheet sheet = new Spreadsheet();
        Scanner scanner = new Scanner(System.in);

        String currentFileName = "input.txt";

        try {
            sheet.loadFromFile(currentFileName);
            System.out.println("Table loaded successfully from " + currentFileName);
        } catch (InvalidCellException e) {
            System.out.println("Input error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("File could not be loaded: " + e.getMessage());
            currentFileName = null;
        }

        System.out.println("Type 'help' to see available commands.\n");

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

            } else if (commandLine.equalsIgnoreCase("close")) {
                sheet.clear();
                currentFileName = null;
                System.out.println("File closed.");

            } else if (commandLine.startsWith("open ")) {
                currentFileName = handleOpenCommand(sheet, commandLine, currentFileName);

            } else if (commandLine.equalsIgnoreCase("save")) {
                handleSaveCommand(sheet, currentFileName);

            } else if (commandLine.startsWith("save as ")) {
                currentFileName = handleSaveAsCommand(sheet, commandLine, currentFileName);

            } else if (commandLine.startsWith("edit")) {
                handleEditCommand(sheet, commandLine);

            } else if (commandLine.startsWith("raw")) {
                handleRawCommand(sheet, commandLine);

            } else if (commandLine.isEmpty()) {
                // Ignore empty command

            } else {
                System.out.println("Unknown command. Type 'help' for available commands.");
            }
        }

        System.out.println("Program closed.");
    }

    private static String handleOpenCommand(Spreadsheet sheet, String commandLine, String oldFileName) {
        String[] parts = commandLine.split("\\s+", 2);

        if (parts.length < 2) {
            System.out.println("Invalid open command. Example: open input.txt");
            return oldFileName;
        }

        String fileName = parts[1];

        try {
            sheet.loadFromFile(fileName);
            System.out.println("File opened: " + fileName);
            return fileName;
        } catch (InvalidCellException e) {
            System.out.println("Input error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }

        return oldFileName;
    }

    private static void handleSaveCommand(Spreadsheet sheet, String currentFileName) {
        if (currentFileName == null) {
            System.out.println("No active file. Use 'save as filename.txt' first.");
            return;
        }

        try {
            sheet.saveToFile(currentFileName);
            System.out.println("File saved: " + currentFileName);
        } catch (IOException e) {
            System.out.println("Save error: " + e.getMessage());
        }
    }

    private static String handleSaveAsCommand(Spreadsheet sheet, String commandLine, String oldFileName) {
        String[] parts = commandLine.split("\\s+", 3);

        if (parts.length < 3) {
            System.out.println("Invalid save as command. Example: save as output.txt");
            return oldFileName;
        }

        String fileName = parts[2];

        try {
            sheet.saveToFile(fileName);
            System.out.println("File saved as: " + fileName);
            return fileName;
        } catch (IOException e) {
            System.out.println("Save as error: " + e.getMessage());
            return oldFileName;
        }
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

    private static void handleRawCommand(Spreadsheet sheet, String commandLine) {
        // Expected format: raw row column
        String[] parts = commandLine.split("\\s+");

        if (parts.length != 3) {
            System.out.println("Invalid raw command. Example: raw 1 2");
            return;
        }

        try {
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);

            System.out.println(sheet.getCellRawContent(row, col));

        } catch (NumberFormatException e) {
            System.out.println("Row and column must be valid numbers.");
        }
    }

    private static void printHelp() {
        System.out.println("Available commands:");
        System.out.println("open filename.txt       - Opens a file");
        System.out.println("close                   - Closes the current table");
        System.out.println("save                    - Saves the current file");
        System.out.println("save as filename.txt    - Saves the table into a new file");
        System.out.println("print                   - Displays the table");
        System.out.println("edit row col value      - Changes a cell value");
        System.out.println("raw row col             - Shows original cell content");
        System.out.println("help                    - Shows available commands");
        System.out.println("exit                    - Closes the program");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("open input.txt");
        System.out.println("print");
        System.out.println("edit 1 2 100");
        System.out.println("edit 2 3 \"Hello\"");
        System.out.println("edit 3 1 =R1C1+R1C2");
        System.out.println("raw 3 1");
        System.out.println("save");
        System.out.println("save as output.txt");
    }
}