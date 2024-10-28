package rs.raf.calculator.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
public class NumberLit extends Expr {
    private double value;

    public NumberLit(Location location, double value) {
        super(location);
        this.value = value;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
        pp.node("number", () -> pp.terminal(Objects.toString(value)));
    }

}
