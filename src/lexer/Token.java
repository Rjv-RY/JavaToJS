package lexer;

public class Token {
    private TokenType type;
    private String value;

    public Token(TokenType type, String value){
        this.value = value;
        this.type = type;
    }

    public TokenType getType(){
        return type;
    }

    public String getValue(){
        return value;
    }

    @Override
    public String toString(){
        return "Token{" + "type=" + type + ", value='" + value + '\'' + '}';
    }
}
