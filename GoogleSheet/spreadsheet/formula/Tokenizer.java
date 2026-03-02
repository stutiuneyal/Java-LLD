package spreadsheet.formula;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {

    private final String s;
    private int i = 0;

    private static final Pattern CELL = Pattern.compile("^[A-Za-z]+[0-9]+");

    public Tokenizer(String expr) {
        this.s = expr;
    }

    public List<Token> tokenize() {

        List<Token> tokens = new ArrayList<>();

        while (i < s.length()) {

            char c = s.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (c == '(') {
                tokens.add(new Token(TokenType.LPAREN, "("));
                i++;
                continue;
            }
            if (c == ')') {
                tokens.add(new Token(TokenType.RPAREN, ")"));
                i++;
                continue;
            }

            if (isOp(c)) {
                tokens.add(new Token(TokenType.OP, String.valueOf(c)));
                i++;
                continue;
            }

            if (Character.isDigit(c) || c == '.') {
                int start = i;
                i++;

                // 200 -> single token
                // 123.45 -> single token
                while (i < s.length() && (Character.isDigit(s.charAt(i)) || s.charAt(i) == '.')) {
                    i++;
                }

                tokens.add(new Token(TokenType.NUMBER, s.substring(start, i)));
                continue;
            }

            // A1 + B1 + C1 -> [A1,B1,C1]
            Matcher m = CELL.matcher(s.substring(i));
            if (m.find()) {
                String ref = m.group(); // only first match
                tokens.add(new Token(TokenType.CELL, ref));
                i += ref.length();
                continue;
            }

            throw new IllegalArgumentException("Invalid Token near: '" + s.substring(i) + "'");
        }

        return tokens;
    }

    private boolean isOp(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }
}
