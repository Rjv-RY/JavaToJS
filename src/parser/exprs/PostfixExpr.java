package parser.exprs;

import lexer.Token;
import parser.Expr;

public class PostfixExpr extends Expr{
    private final Expr left;
    private final Token operator;
    
    public PostfixExpr(Expr left, Token operator) {
        this.left = left;
        this.operator = operator;
    }
    
    public Token getOperator(){
        return operator;
    }
    
    public Expr getOperand(){
        return left;
    }
}
