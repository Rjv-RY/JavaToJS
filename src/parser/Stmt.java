import lexer.Token;
import java.util.List;

//for statements

public abstract class Stmt {
    }

class ExpressionStmt extends Stmt{
    private final Expr expr;

    public ExpressionStmt(Expr expr){
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
    private final Token name;
    private final Expr initializer;

    public VarStmt(Token name, Expr initializer){
        this.name = name;
        this.initializer = initializer;
    }

    public Token getName(){
        return name;
    }

    public Expr getInitialzer(){
        return initializer;
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





