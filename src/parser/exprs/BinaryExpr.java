package parser.exprs;

import lexer.Token;
import parser.Expr;

public class BinaryExpr extends Expr{
    private final Expr left;
    private final Expr right;
    private final Token operator;

    public BinaryExpr(Expr left, Token operator, Expr right){
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public Expr getLeft(){
        return left;
    }

    public Expr getRight(){
        return right;
    }

    public Token getOperator(){
        return operator;
    }
}