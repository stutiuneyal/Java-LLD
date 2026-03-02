package spreadsheet.formula;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import spreadsheet.Spreadsheet;

public class FormulaEngine {

    public CompiledFormula compile(String expr) {

        Tokenizer tokenizer = new Tokenizer(expr);
        List<Token> infix = tokenizer.tokenize();
        List<Token> rpn = ShuntingYard.toRpn(infix);

        Set<String> refs = new HashSet<>();
        for (Token t : rpn) {
            if (t.getType() == TokenType.CELL) {
                refs.add(t.getText().trim().toUpperCase());
            }
        }

        return new CompiledFormula(rpn, refs);
    }

    public double evaluate(String cellId, CompiledFormula formula, Spreadsheet sheet, Set<String> stack) {

        Stack<Double> st = new Stack<>();

        for (Token t : formula.getRpn()) {
            switch (t.getType()) {
                case NUMBER:
                    st.push(Double.parseDouble(t.getText()));
                    break;

                case CELL:
                    st.push(sheet.evalCell(t.getText().trim().toUpperCase(), stack));
                    break;

                case OP:
                    double b = st.pop();
                    double a = st.pop();
                    st.push(applyOp(a, b, t.getText()));
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected token in rpn: " + t);
            }
        }

        return st.isEmpty() ? 0d : st.pop();
    }

    private double applyOp(double a, double b, String op) {
        switch (op) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                return a / b;
            default:
                throw new IllegalArgumentException("Unknown op: " + op);
        }
    }
}
