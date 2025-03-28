package parser.exprs;

import parser.Expr;

public class DefaultExpr extends Expr {
    @Override
    public String toString() {
        return "null"; // Or some default value like "0" for numbers, "false" for booleans
    }
}
