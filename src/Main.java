package app;

import app.core.Spreadsheet;
import app.exception.InvalidCellException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Spreadsheet sheet = new Spreadsheet();

        String fileName = "input.txt";

        try {
            sheet.loadFromFile(fileName);
            System.out.println("Table loaded successfully.\n");
            sheet.printTable();
        } catch (InvalidCellException e) {
            System.out.println("Input error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
    }
}