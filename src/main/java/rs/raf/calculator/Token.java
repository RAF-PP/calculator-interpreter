package rs.raf.calculator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line; // [location]


    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
