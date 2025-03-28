package parser.stmts;

import parser.Expr;
import parser.Stmt;

public class PrintStmt extends Stmt{
    private final Expr expr;

    public PrintStmt(Expr expr){
        this.expr = expr;
    }

    public Expr getExpr(){
        return expr;
    }
}
