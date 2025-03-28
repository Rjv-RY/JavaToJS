package parser.stmts;

import java.util.List;
import lexer.Token;
import parser.Expr;
import parser.Stmt;

public class ArrayAssignmentStmt extends Stmt {
    private final Token arrayName;
    private final List<Expr> indices;
    private final Expr value;

    public ArrayAssignmentStmt(Token arrayName, List<Expr> indices, Expr value){
        this.arrayName = arrayName;
        this.indices = indices;
        this.value = value;
    }

    public Token getArrayName(){
        return arrayName;
    }

    public List<Expr> getIndices(){
        return indices;
    }

    public Expr getValue(){
        return value;
    }
}
