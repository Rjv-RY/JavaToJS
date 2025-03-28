package parser.stmts;

import parser.Stmt;
import parser.Expr;

public class ExprStmt extends Stmt{
    private final Expr expr;

    public ExprStmt(Expr expr){
        this.expr = expr;
    }

    public Expr getExpr(){
        return expr;
    }
}
