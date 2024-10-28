package rs.raf.calculator.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/** Base class for AST nodes.  */
@Getter
@Setter
@EqualsAndHashCode
public abstract class Tree {
    private Location location;

    public Tree(Location location) {
        this.location = location;
    }

    public abstract void prettyPrint(ASTPrettyPrinter pp);
}