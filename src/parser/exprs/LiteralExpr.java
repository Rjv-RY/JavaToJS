package parser.exprs;

import lexer.TokenType;
import parser.Expr;

public class LiteralExpr extends Expr{
    private final Object value;
    private final TokenType tokenType;
    
    public LiteralExpr(Object value, TokenType tokenType){
        this.value = value;
        this.tokenType = tokenType;
    }

    public Object getValue(){
        return value;
    }
    public TokenType getTokenType() {  // Add this getter
        return tokenType;
    }
}
