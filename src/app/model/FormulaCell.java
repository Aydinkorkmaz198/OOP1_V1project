package app.model;

import app.core.Spreadsheet;
import app.exception.EvaluationException;

import java.util.HashSet;
import java.util.Set;

public class FormulaCell extends Cell {

    public FormulaCell(String rawContent) {
        super(rawContent);
    }

    @Override
    public String getDisplayValue(Spreadsheet sheet) {
        try {
            double result = evaluate(sheet, new HashSet<>());

            // Remove unnecessary decimal part
            if (result == (long) result) {
                return String.valueOf((long) result);
            }

            return String.valueOf(result);

        } catch (EvaluationException e) {
            return "ERROR";
        }
    }

    @Override
    public double getNumericValue(Spreadsheet sheet) throws EvaluationException {
        return evaluate(sheet, new HashSet<>());
    }

    // Main recursive evaluation method
    private double evaluate(Spreadsheet sheet, Set<String> visited)
            throws EvaluationException {

        String expression = rawContent.substring(1).trim();

        char operator = findMainOperator(expression);

        if (operator == '\0') {
            return resolveOperand(expression, sheet, visited);
        }

        int operatorIndex = findOperatorIndex(expression, operator);

        String leftText = expression.substring(0, operatorIndex).trim();
        String rightText = expression.substring(operatorIndex + 1).trim();

        double leftValue = resolveOperand(leftText, sheet, visited);
        double rightValue = resolveOperand(rightText, sheet, visited);

        switch (operator) {
            case '+':
                return leftValue + rightValue;

            case '-':
                return leftValue - rightValue;

            case '*':
                return leftValue * rightValue;

            case '/':
                if (rightValue == 0) {
                    throw new EvaluationException("Division by zero");
                }

                return leftValue / rightValue;

            case '^':
                return Math.pow(leftValue, rightValue);

            default:
                throw new EvaluationException("Unknown operator");
        }
    }

    private double resolveOperand(String operand,
                                  Spreadsheet sheet,
                                  Set<String> visited)
            throws EvaluationException {

        // Integer
        if (operand.matches("[+-]?\\d+")) {
            return Integer.parseInt(operand);
        }

        // Double
        if (operand.matches("[+-]?\\d+\\.\\d+")) {
            return Double.parseDouble(operand);
        }

        // Cell reference
        if (operand.matches("R\\d+C\\d+")) {

            if (visited.contains(operand)) {
                throw new EvaluationException("Circular reference");
            }

            visited.add(operand);

            int cIndex = operand.indexOf('C');

            int row = Integer.parseInt(operand.substring(1, cIndex));
            int col = Integer.parseInt(operand.substring(cIndex + 1));

            Cell referencedCell = sheet.getCell(row, col);

            double value;

            if (referencedCell instanceof FormulaCell) {

                // Recursive formula evaluation
                value = ((FormulaCell) referencedCell)
                        .evaluate(sheet, visited);

            } else {
                value = referencedCell.getNumericValue(sheet);
            }

            visited.remove(operand);

            return value;
        }

        throw new EvaluationException("Invalid operand");
    }

    private char findMainOperator(String expression) {

        // Skip first char to allow negative numbers
        for (int i = 1; i < expression.length(); i++) {

            char ch = expression.charAt(i);

            if (ch == '+' ||
                    ch == '-' ||
                    ch == '*' ||
                    ch == '/' ||
                    ch == '^') {

                return ch;
            }
        }

        return '\0';
    }

    private int findOperatorIndex(String expression, char operator) {

        for (int i = 1; i < expression.length(); i++) {

            if (expression.charAt(i) == operator) {
                return i;
            }
        }

        return -1;
    }
}