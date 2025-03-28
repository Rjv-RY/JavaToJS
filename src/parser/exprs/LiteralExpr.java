package parser.exprs;

import parser.Expr;

public class LiteralExpr extends Expr{
    private final Object value;

    public LiteralExpr(Object value){
        this.value = value;
    }

    public Object getValue(){
        return value;
    }
}
