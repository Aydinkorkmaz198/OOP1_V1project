package app.model;

import app.core.Spreadsheet;
import app.exception.EvaluationException;

public class FormulaCell extends Cell {

    public FormulaCell(String rawContent) {
        super(rawContent);
    }

    @Override
    public String getDisplayValue(Spreadsheet sheet) {
        try {
            double result = getNumericValue(sheet);

            // Show integer-like results without decimal part
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
        String expression = rawContent.trim();

        if (!expression.startsWith("=")) {
            throw new EvaluationException("Formula must start with '='");
        }

        expression = expression.substring(1).trim();

        // Supports simple formulas like:
        // =10+5
        // =R1C1+R1C2
        // =10/R2C1
        char operator = findMainOperator(expression);

        if (operator == '\0') {
            return resolveOperand(expression, sheet);
        }

        int operatorIndex = findOperatorIndex(expression, operator);

        if (operatorIndex <= 0 || operatorIndex >= expression.length() - 1) {
            throw new EvaluationException("Invalid formula format");
        }

        String leftText = expression.substring(0, operatorIndex).trim();
        String rightText = expression.substring(operatorIndex + 1).trim();

        double leftValue = resolveOperand(leftText, sheet);
        double rightValue = resolveOperand(rightText, sheet);

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
                throw new EvaluationException("Unsupported operator");
        }
    }

    private char findMainOperator(String expression) {
        // Skips the first character so negative literals like -10 are not treated as an operator
        for (int i = 1; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^') {
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

    private double resolveOperand(String operand, Spreadsheet sheet) throws EvaluationException {
        if (operand.matches("[+-]?\\d+")) {
            return Integer.parseInt(operand);
        }

        if (operand.matches("[+-]?\\d+\\.\\d+")) {
            return Double.parseDouble(operand);
        }

        if (operand.matches("R\\d+C\\d+")) {
            int cIndex = operand.indexOf('C');

            int row = Integer.parseInt(operand.substring(1, cIndex));
            int col = Integer.parseInt(operand.substring(cIndex + 1));

            Cell referencedCell = sheet.getCell(row, col);
            return referencedCell.getNumericValue(sheet);
        }

        throw new EvaluationException("Invalid operand: " + operand);
    }
}