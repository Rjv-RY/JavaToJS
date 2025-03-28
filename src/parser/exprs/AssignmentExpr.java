package parser.exprs;

import parser.Expr;
import lexer.Token;

public class AssignmentExpr extends Expr{
    private final Token name;
    private final Token assign;
    private final Expr right;

    public AssignmentExpr(Token name, Token assign, Expr right){
        this.name = name;
        this.right = right;
        this.assign = assign;
    }

    public Token getName(){
        return name;
    }

    public Expr getRight(){
        return right;
    }

    public Token getAssign(){
        return assign;
    }
}
