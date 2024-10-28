package rs.raf.calculator.ast;

import java.io.PrintStream;

public class ASTPrettyPrinter {
    private int indent = 0;
    private final PrintStream output;

    public ASTPrettyPrinter(PrintStream output) {
        this.output = output;
    }

    private String indentStr() {
        return " ".repeat(4 * indent);
    }

    public void node(String name, Runnable subprinter) {
        var indentStr = indentStr();
        output.printf("%s%s {\n", indentStr, name);
        try {
            indent++;
            subprinter.run();
        } finally {
            indent--;
        }
        output.printf("%s}\n", indentStr);
    }

    public void terminal(String value) {
        var indentStr = indentStr();
        output.printf("%s%s;\n", indentStr, value);
    }
}
