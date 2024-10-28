package rs.raf.calculator.ast;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Position {

    private final int line;
    private final int column;

    public Position(int line, int column) {
        this.line = line;
        this.column = column;
    }

    /** Returns {@code true} iff the position {@code other} is before {@code
    this} in a file.  */
    public boolean lessThan(Position other) {
        if (other.line < this.line)
            return true;
        if (other.line == this.line)
            return other.column < this.column;

        /* Necessarily on a later line, hence greater.  */
        return false;
    }

    @Override
    public String toString() {
        return "Position{" +
                "line=" + line +
                ", column=" + column +
                '}';
    }
}