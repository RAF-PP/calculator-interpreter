package rs.raf.calculator;

import rs.raf.Main;
import rs.raf.calculator.ast.*;

import java.util.ArrayList;
import java.util.List;

import static rs.raf.calculator.TokenType.*;

public class Parser {
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Expr expression(){
        return additionExpr();
    }

    private Expr additionExpr() {
        Expr expr = multiplicationExpr();

        while (match(PLUS, MINUS)) {
            Token operator = previous();
            Expr.Operation op = operator.type == TokenType.PLUS ? Expr.Operation.ADD : Expr.Operation.SUB;
            Expr right = multiplicationExpr();
            expr = new Expr(expr.getLocation().span(right.getLocation()), op, expr, right);
        }

        return expr;
    }

    private Expr multiplicationExpr() {
        Expr expr = exponentExpr();

        while (match(STAR, SLASH)) {
            Token operator = previous();
            Expr.Operation op = operator.type == TokenType.STAR ? Expr.Operation.MUL : Expr.Operation.DIV;
            Expr right = exponentExpr();
            expr = new Expr(expr.getLocation().span(right.getLocation()), op, expr, right);
        }

        return expr;
    }

    private Expr exponentExpr() {
        Expr expr = atom();

        if (match(CARET)) {
            Expr right = exponentExpr(); // Right-associative
            expr = new Expr(expr.getLocation().span(right.getLocation()), Expr.Operation.POW, expr, right);
        }

        return expr;
    }

    private Expr atom() {
        if (match(NUMBER)) {
            Token numberToken = previous();
            return new NumberLit(numberToken.getLocation(), (Double) numberToken.literal);
        }

        if (match(IDENTIFIER)) {
            Token identifierToken = previous();
            return new VarRef(identifierToken.getLocation(), identifierToken.lexeme);
        }

        if (match(OPEN_BRACKET)) {
            Expr expr = expression();
            consume(CLOSED_BRACKET, "Expect ')' after expression.");
            return expr;
        }

        if (match(VECTOR_OPEN)) {
            return vectorLiteral();
        }

        throw error(previous(), "Expect expression.");
    }

    private Expr vectorLiteral() {
        Token startToken = previous();
        List<Expr> elements = new ArrayList<>();

        if (!check(VECTOR_CLOSE)) {
            do {
                elements.add(expression());
            } while (match(COMMA));
        }

        consume(VECTOR_CLOSE, "Expect '>' at the end of vector literal.");
        return new VectorExpr(startToken.getLocation().span(previous().getLocation()), elements);
    }

    public StatementList parse() {
        List<Statement> statements = new ArrayList<>();

        while (!isAtEnd()) {
            statements.add(statement());
        }

        if (statements.isEmpty()) {
            return new StatementList(new Location(new Position(0, 0), new Position(0, 0)), new ArrayList<>());
        }

        return new StatementList(statements.getFirst().getLocation().span(statements.getLast().getLocation()), statements);    }

    private Statement statement(){
        if (match(PRINT)) return printStatement();
        if (match(LET)) return declarationStatement();
        return expressionStatement();
    }

    private Statement expressionStatement(){
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after statement.");
        return new ExprStmt(expr.getLocation(), expr);
    }

    private Statement printStatement(){
        consume(OPEN_BRACKET, "Expect ( after print keyword.");
        List<Expr> expressions = new ArrayList<>();
        expressions.add(expression());

        // Handle multiple expressions separated by commas.
        while (match(COMMA)) {
            expressions.add(expression());
        }
        consume(CLOSED_BRACKET, "Expect ')' after print arguments.");
        consume(SEMICOLON, "Expect ';' after statement.");
        return new PrintStmt(expressions.getFirst().getLocation().span(previous().getLocation()), expressions);
    }

    private Statement declarationStatement(){
        Token identifier = consume(IDENTIFIER, "Expect Identifier after keyword let.");
        String name = identifier.getLexeme();
        consume(ASSIGN, "Expect assign after Identifier.");
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after statement.");

        return new Declaration(identifier.getLocation().span(previous().getLocation()), name, expr);
    }

    /** Check if current token matches any of the given types. Returns {@code true} if so and consumes token */
    private boolean match(TokenType... types){
        for (TokenType type: types){
            if (check(type)){
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    /** Consumes current token*/
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(previous(), message);
    }

    private ParseError error(Token token, String message) {
        Main.error(token, message);
        return new ParseError();
    }
}