package rs.raf.calculator;

import rs.raf.Main;
import rs.raf.calculator.ast.Location;
import rs.raf.calculator.ast.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static rs.raf.calculator.TokenType.*;

public class Scanner {
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("let", LET);
        keywords.put("print", PRINT);
    }

    private final String source;
    private final List<Token> tokens = new ArrayList<Token>();
    private int start = 0; // first char in lexeme being scanned
    private int current = 0; // current char being considered
    private int line = 1;
    private int column = 0;
    private int startColumn = 0;

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            startColumn = column;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, new Location(new Position(line, column), new Position(line, column))));
        return tokens;
    }

    private void scanToken() {
        char c = getNextChar();
        switch (c) {
            case '(': addToken(OPEN_BRACKET); break;
            case ')': addToken(CLOSED_BRACKET); break;
            case '+': addToken(PLUS); break;
            case '-': addToken(MINUS); break;
            case '*': addToken(STAR); break;
            case '^': addToken(CARET); break;
            case '<': addToken(VECTOR_OPEN); break;
            case '>': addToken(VECTOR_CLOSE); break;
            case ',': addToken(COMMA); break;
            case ';': addToken(SEMICOLON); break;
            case '=': addToken(ASSIGN); break;
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) getNextChar();
                } else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                column++;
                break;
            case '\n':
                line++;
                column = 0;
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Position startPos = new Position(line, startColumn);
                    Position endPos = new Position(line, column);
                    Main.error(new Location(
                            startPos, endPos
                    ), "Unexpected character.");
                }
                break;
        }
    }

    private void number() {
        while (isDigit(peek())) getNextChar();

        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."

            do getNextChar();
            while (isDigit(peek()));
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) getNextChar();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        Position startPos = new Position(line, startColumn);
        Position endPos = new Position(line, column);
        tokens.add(new Token(type, text, literal, new Location(startPos, endPos)));
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        column++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private char getNextChar() {
        column++;
        return source.charAt(current++);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}