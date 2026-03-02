package spreadsheet.formula;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/*
Infix -> RPN(Postfix)

Algorithm:
 -> stack to hold the operators
 -> queue/list -> for the output

Process tokens from L->R in a single pass
*/
public class ShuntingYard {

    public static List<Token> toRpn(List<Token> infix) {

        List<Token> tokens = new ArrayList<>();
        Stack<Token> ops = new Stack<>(); // stack

        // (3+A1)*7 -> 3,A1,+,7,*
        for (Token t : infix) {

            switch (t.getType()) {
                case NUMBER:
                case CELL:
                    tokens.add(t);
                    break;
                case OP:
                    while (!ops.isEmpty() && ops.peek().getType() == TokenType.OP
                            && precedence(ops.peek()) >= precedence(t)) {
                        tokens.add(ops.pop());
                    }
                    ops.push(t);
                    break;

                case LPAREN:
                    ops.push(t);
                    break;

                case RPAREN:

                    while (!ops.isEmpty() && ops.peek().getType() != TokenType.LPAREN) {
                        tokens.add(ops.pop());
                    }

                    if (ops.isEmpty() || ops.peek().getType() != TokenType.LPAREN) {
                        throw new IllegalArgumentException("Mismatched Parenthesis");
                    }

                    ops.pop(); // popping '('
                    break;

                default:
                    throw new IllegalArgumentException("Unexpected Token: " + t);

            }
        }

        while (!ops.isEmpty()) {
            Token top = ops.pop();
            if (top.getType() == TokenType.LPAREN || top.getType() == TokenType.RPAREN) {
                throw new IllegalArgumentException("Mismatched Parenthesis");
            }
            tokens.add(top);
        }

        return tokens;

    }

    private static int precedence(Token t) {
        if (t.getText().equals("*") || t.getText().equals("/")) {
            return 2;
        }
        if (t.getText().equals("+") || t.getText().equals("-")) {
            return 1;
        }

        return 0;

    }
}
