package parser.exprs;

import java.util.List;
import parser.Expr;

public class ArrayLiteralExpr extends Expr {
    private final List<Expr> elements;

    public ArrayLiteralExpr(List<Expr> elements) {
        this.elements = elements;
    }

    public List<Expr> getElements() {
        return elements;
    }
}