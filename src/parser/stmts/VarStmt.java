package parser.stmts;

import lexer.Token;
import parser.Expr;
import parser.Stmt;

public class VarStmt extends Stmt{
    private final Token type;
    private final Token name;
    private final Expr initializer;
    private final boolean isArray;
    private int dimensions;

    public VarStmt(Token type, Token name, Expr initializer, boolean isArray, int dimensions){
        this.type = type;
        this.name = name;
        this.initializer = initializer;
        this.isArray = isArray;

        this.dimensions = dimensions;
    }

    public int getDimensions() {
        return dimensions;
    }

    public Token getType(){
        return type;
    }

    public Token getName(){
        return name;
    }

    public Expr getInitialzer(){
        return initializer;
    }

    public boolean isArray(){
        return isArray;
    }
}
