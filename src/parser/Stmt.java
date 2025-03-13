import lexer.Token;
import java.util.List;

//for statements

public abstract class Stmt {
    }

class ExprStmt extends Stmt{
    private final Expr expr;

    public ExprStmt(Expr expr){
        this.expr = expr;
    }

    public Expr getExpr(){
        return expr;
    }
}

class PrintStmt extends Stmt{
    private final Expr expr;

    public PrintStmt(Expr expr){
        this.expr = expr;
    }

    public Expr getExpr(){
        return expr;
    }
}

class VarStmt extends Stmt{
    private final Token type;
    private final Token name;
    private final Expr initializer;
    private final boolean isArray;

    public VarStmt(Token type, Token name, Expr initializer, boolean isArray){
        this.type = type;
        this.name = name;
        this.initializer = initializer;
        this.isArray = isArray;
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

class BlockStmt extends Stmt{
    private final List<Stmt> statements;

    public BlockStmt(List<Stmt> statements){
        this.statements = statements;
    }

    public List<Stmt> getStatements(){
        return statements;
    }
}

class IfStmt extends Stmt {
    private final Expr condition;
    private final Stmt thenBranch;
    private final Stmt elseBranch;

    public IfStmt(Expr condition, Stmt thenBranch, Stmt elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public Expr getCondition() {
        return condition;
    }

    public Stmt getThenBranch() {
        return thenBranch;
    }

    public Stmt getElseBranch() {
        return elseBranch;
    }
}

class WhileStmt extends Stmt {
    private final Expr condition;
    private final Stmt body;

    public WhileStmt(Expr condition, Stmt body) {
        this.condition = condition;
        this.body = body;
    }

    public Expr getCondition() {
        return condition;
    }

    public Stmt getBody() {
        return body;
    }
}





