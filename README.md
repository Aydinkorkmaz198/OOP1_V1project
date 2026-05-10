# Java Spreadsheet Application

This is a console-based spreadsheet application developed in Java.

## Features

- Reads spreadsheet data from text files
- Supports Integer, Double, String, Empty, and Formula cells
- Supports formulas with:
  - Addition
  - Subtraction
  - Multiplication
  - Division
  - Exponentiation
  - Parentheses
  - Cell references such as R1C1
- Detects division by zero
- Detects circular formula references
- Supports commands:
  - open
  - close
  - save
  - save as
  - print
  - edit
  - raw
  - help
  - exit

## Example Commands

```text
print
edit 1 2 100
edit 3 1 =R1C1+R1C2
save
save as output.txt
open output.txt
exit





Project Structure
src/app
├── Main.java
├── core
│   └── Spreadsheet.java
├── exception
│   ├── EvaluationException.java
│   └── InvalidCellException.java
├── factory
│   └── CellFactory.java
└── model
    ├── Cell.java
    ├── EmptyCell.java
    ├── IntegerCell.java
    ├── DoubleCell.java
    ├── StringCell.java
    └── FormulaCell.java
