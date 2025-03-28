package parser.exprs;

import lexer.Token;
import parser.Expr;

public class UnaryExpr extends Expr{
    private final Expr right;
    private final Token operator;

    public UnaryExpr (Token operator, Expr right){
        this.right = right;
        this.operator = operator;
    }

    public Token getOperator(){
        return operator;
    }

    public Expr getRight(){
        return right;
    }
}
