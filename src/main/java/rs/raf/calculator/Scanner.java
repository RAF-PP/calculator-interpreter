package rs.raf.calculator;

import rs.raf.Main;

import java.util.ArrayList;
import java.util.List;

import static rs.raf.calculator.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<Token>();
    private int start = 0; // first char in lexeme being scanned
    private int current = 0; // current char being considered
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
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
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) getNextChar();
                } else {
                    addToken(SLASH);
                }
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else {
                    Main.error(line, "Unexpected character.");
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

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
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
        return source.charAt(current++);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }
}