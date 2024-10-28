package rs.raf.calculator;

public enum TokenType {
    OPEN_BRACKET, CLOSED_BRACKET, COMMA, SEMICOLON,
    VECTOR_OPEN, VECTOR_CLOSE, ASSIGN,

    CARET, STAR, SLASH, PLUS, MINUS,

    IDENTIFIER, NUMBER,

    LET, PRINT,

    EOF
}
