package rs.raf.calculator.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
public class VarRef extends Expr {
    private String varName;

    public VarRef(Location location, String varName) {
        super(location);
        this.varName = varName;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
        pp.node("let", () -> pp.terminal(varName));
    }
}