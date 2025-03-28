package parser.exprs;

import parser.Expr;

public class VariableExpr extends Expr{
    private final String name;

    public VariableExpr(String name){
        this.name = name;
    }

    public String getVar(){
        return name;
    }
}
