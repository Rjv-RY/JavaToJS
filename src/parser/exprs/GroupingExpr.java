package parser.exprs;

import parser.Expr;

public class GroupingExpr extends Expr{
    private final Expr expr;

    public GroupingExpr(Expr expr){
        this.expr = expr;
    }

    public Expr getExpr(){
        return expr;
    }
}