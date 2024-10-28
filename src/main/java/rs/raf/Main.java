package rs.raf;

import rs.raf.calculator.*;
import rs.raf.calculator.ast.ASTPrettyPrinter;
import rs.raf.calculator.ast.Location;
import rs.raf.calculator.ast.StatementList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Main {
    private static final Calculator calculator = new Calculator();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
            run(content.toString());
        }

        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while (true) {
            System.out.print("> ");
            String line = reader.readLine();

            if (line == null || line.equalsIgnoreCase("exit")) {
                break;
            }

            run(line);
            hadError = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        if (hadError) return;
        for (Token token : tokens) {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);
        StatementList statementList = parser.parse();

        if (hadError) return;
        var pp = new ASTPrettyPrinter(System.out);
        statementList.prettyPrint(pp);
    }

    public static void error(Location location, String message) {
        report(location, "", message);
    }

    private static void report(Location location, String where,
                               String message) {
        System.err.println(
                "[location " + location + "] Error" + where + ": " + message);
        hadError = true;
    }

    public static void error(Token token, String message) {
        if (token.getType() == TokenType.EOF) {
            report(token.getLocation(), " at end", message);
        } else {
            report(token.getLocation(), " at '" + token.getLexeme() + "'", message);
        }
    }
}