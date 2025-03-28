package parser.exprs;

import java.util.List;
import lexer.Token;
import parser.Expr;

public class NewArrayExpr extends Expr{
    private final Token type;
    private final List<Expr> dimensions;

    public NewArrayExpr(Token type, List<Expr> dimensions) {
        this.type = type;
        this.dimensions = dimensions;
    }

    public Token getType() {
        return type;
    }

    public List<Expr> getDimensions() {
        return dimensions;
    }
}
