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

    private double evaluate(Spreadsheet sheet, Set<String> visited) throws EvaluationException {
        if (!rawContent.trim().startsWith("=")) {
            throw new EvaluationException("Formula must start with '='");
        }

        String expression = rawContent.trim().substring(1);
        Parser parser = new Parser(expression, sheet, visited);

        double result = parser.parseExpression();

        parser.skipSpaces();

        if (!parser.isAtEndRaw()) {
            throw new EvaluationException("Unexpected character");
        }

        return result;
    }

    private class Parser {
        private final String expression;
        private final Spreadsheet sheet;
        private final Set<String> visited;
        private int position;

        public Parser(String expression, Spreadsheet sheet, Set<String> visited) {
            this.expression = expression;
            this.sheet = sheet;
            this.visited = visited;
            this.position = 0;
        }

        // Handles + and -
        public double parseExpression() throws EvaluationException {
            double value = parseTerm();

            while (true) {
                skipSpaces();

                if (match('+')) {
                    value += parseTerm();
                } else if (match('-')) {
                    value -= parseTerm();
                } else {
                    break;
                }
            }

            return value;
        }

        // Handles * and /
        private double parseTerm() throws EvaluationException {
            double value = parsePower();

            while (true) {
                skipSpaces();

                if (match('*')) {
                    value *= parsePower();
                } else if (match('/')) {
                    double divisor = parsePower();

                    if (divisor == 0) {
                        throw new EvaluationException("Division by zero");
                    }

                    value /= divisor;
                } else {
                    break;
                }
            }

            return value;
        }

        // Handles exponentiation
        private double parsePower() throws EvaluationException {
            double value = parseUnary();

            skipSpaces();

            if (match('^')) {
                double exponent = parsePower();
                value = Math.pow(value, exponent);
            }

            return value;
        }

        // Handles unary + and -
        private double parseUnary() throws EvaluationException {
            skipSpaces();

            if (match('+')) {
                return parseUnary();
            }

            if (match('-')) {
                return -parseUnary();
            }

            return parsePrimary();
        }

        // Handles numbers, references, and parentheses
        private double parsePrimary() throws EvaluationException {
            skipSpaces();

            if (match('(')) {
                double value = parseExpression();

                skipSpaces();

                if (!match(')')) {
                    throw new EvaluationException("Missing closing parenthesis");
                }

                return value;
            }

            if (isDigit(peek())) {
                return parseNumber();
            }

            if (peek() == 'R') {
                return parseCellReference();
            }

            throw new EvaluationException("Invalid formula element");
        }

        private double parseNumber() throws EvaluationException {
            skipSpaces();

            int start = position;
            boolean hasDot = false;

            while (!isAtEndRaw() && (isDigit(peek()) || peek() == '.')) {
                if (peek() == '.') {
                    if (hasDot) {
                        throw new EvaluationException("Invalid number");
                    }

                    hasDot = true;
                }

                position++;
            }

            String numberText = expression.substring(start, position);

            try {
                return Double.parseDouble(numberText);
            } catch (NumberFormatException e) {
                throw new EvaluationException("Invalid number");
            }
        }

        private double parseCellReference() throws EvaluationException {
            skipSpaces();

            int start = position;

            if (!match('R')) {
                throw new EvaluationException("Invalid reference");
            }

            int rowStart = position;

            while (!isAtEndRaw() && isDigit(peek())) {
                position++;
            }

            if (rowStart == position) {
                throw new EvaluationException("Missing row number");
            }

            if (!match('C')) {
                throw new EvaluationException("Invalid reference");
            }

            int colStart = position;

            while (!isAtEndRaw() && isDigit(peek())) {
                position++;
            }

            if (colStart == position) {
                throw new EvaluationException("Missing column number");
            }

            String reference = expression.substring(start, position);

            if (visited.contains(reference)) {
                throw new EvaluationException("Circular reference");
            }

            visited.add(reference);

            int row = Integer.parseInt(expression.substring(rowStart, colStart - 1));
            int col = Integer.parseInt(expression.substring(colStart, position));

            Cell referencedCell = sheet.getCell(row, col);

            double value;

            if (referencedCell instanceof FormulaCell) {
                value = ((FormulaCell) referencedCell).evaluate(sheet, visited);
            } else {
                value = referencedCell.getNumericValue(sheet);
            }

            visited.remove(reference);

            return value;
        }

        private boolean match(char expected) {
            skipSpaces();

            if (!isAtEndRaw() && expression.charAt(position) == expected) {
                position++;
                return true;
            }

            return false;
        }

        private char peek() {
            if (isAtEndRaw()) {
                return '\0';
            }

            return expression.charAt(position);
        }

        private void skipSpaces() {
            while (!isAtEndRaw() && Character.isWhitespace(expression.charAt(position))) {
                position++;
            }
        }

        private boolean isAtEndRaw() {
            return position >= expression.length();
        }

        private boolean isDigit(char ch) {
            return ch >= '0' && ch <= '9';
        }
    }
}