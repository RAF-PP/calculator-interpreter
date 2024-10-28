package rs.raf.calculator;

import lombok.Getter;
import lombok.Setter;
import rs.raf.calculator.ast.Location;

@Getter
@Setter
public class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final Location location;


    public Token(TokenType type, String lexeme, Object literal, Location location) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.location = location;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal + " " + location;
    }
}
