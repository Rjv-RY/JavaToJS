import lexer.Token;
import lexer.TokenType;

//for expressions

abstract class Expr {
    
}

class UnaryExpr extends Expr{
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

class AssignmentExpr extends Expr{
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

class VariableExpr extends Expr{
    private final String name;

    public VariableExpr(String name){
        this.name = name;
    }

    public String getVar(){
        return name;
    }
}

class BinaryExpr extends Expr{
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

class LiteralExpr extends Expr{
    private final Object value;

    public LiteralExpr(Object value){
        this.value = value;
    }

    public Object getValue(){
        return value;
    }
}

class GroupingExpr extends Expr{
    private final Expr expr;

    public GroupingExpr(Expr expr){
        this.expr = expr;
    }

    public Expr getExpr(){
        return expr;
    }
}
