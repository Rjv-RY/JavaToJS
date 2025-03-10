package lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    String sourceCode;
    int position;
    int lineNumber;
    int colNumber;
    List<Token> tokens = new ArrayList<>();

    
    
    private static final Map<String, TokenType> keywordMap = new HashMap<>();
    
    static{
        keywordMap.put("if", TokenType.IF);
        keywordMap.put("else", TokenType.ELSE);
        keywordMap.put("while", TokenType.WHILE);
        keywordMap.put("for", TokenType.FOR);
        keywordMap.put("return", TokenType.RETURN);
        keywordMap.put("int", TokenType.INT);
        keywordMap.put("float", TokenType.FLOAT);
        keywordMap.put("boolean", TokenType.BOOLEAN);
        keywordMap.put("char", TokenType.CHAR);
        keywordMap.put("void", TokenType.VOID);
        keywordMap.put("true", TokenType.BOOLEAN_LITERALS);
        keywordMap.put("false", TokenType.BOOLEAN_LITERALS);
        keywordMap.put("print", TokenType.PRINT);
        keywordMap.put("var", TokenType.VAR);
    }
    
    public Lexer(String sourceCode){
        this.sourceCode = sourceCode;
        position = 0;
        lineNumber = 1;
        colNumber = 1;
        this.tokens = new ArrayList<>();
    }
    
    private Character peek(){
        if (position + 1 >= sourceCode.length()){
            return null;
        }
        return sourceCode.charAt(position + 1);
    }
    
    private void advance(){
        if (position >= sourceCode.length()){return;}
        if (sourceCode.charAt(position) == '\n'){
            lineNumber++;
            colNumber = 1;
        } else {
            colNumber++;
        }
        position++;
    }
    
    
    public void tokenize(){
        while (position < sourceCode.length()){
            char current = sourceCode.charAt(position);
            
            if (Character.isWhitespace(current)){
                advance();
            } else if (Character.isDigit(current)){
                StringBuilder number = new StringBuilder();
                boolean floatPoint = false;
                while (position < sourceCode.length() && (Character.isDigit(current) || (current == '.' && !floatPoint))) {
                    if (current == '.') {
                        floatPoint = true;
                        
                        // Check if there is at least one digit after the decimal point
                        if (position + 1 >= sourceCode.length() || !Character.isDigit(sourceCode.charAt(position + 1))) {
                            System.err.println("ERROR: No digits after decimal, invalid float at line " + lineNumber + ", col " + colNumber);
                            break;
                        }
                    }
            
                    number.append(current);
                    advance();
                    
                    if (position < sourceCode.length()) {
                        current = sourceCode.charAt(position);
                    }
                }
                tokens.add(new Token(floatPoint ? TokenType.FLOAT_LITERALS : TokenType.NUMBER_LITERALS, number.toString()));
            } else if (Character.isLetter(current)){
                StringBuilder identifier = new StringBuilder();
                while (position < sourceCode.length() && Character.isLetterOrDigit(current) || current == '_') {
                    identifier.append(current);
                    advance();
                    if(position < sourceCode.length()) {
                        current = sourceCode.charAt(position);
                    }
                }
                String lexeme = identifier.toString();
                TokenType type = keywordMap.getOrDefault(lexeme, TokenType.IDENTIFIER);
                tokens.add(new Token(type, lexeme));
            } else if (current == '"'){
                advance();
                StringBuilder str = new StringBuilder();
                while (position < sourceCode.length() && sourceCode.charAt(position) != '"'){
                    str.append(sourceCode.charAt(position));
                    advance();
                }
                if (position >= sourceCode.length()){
                    System.err.println("ERROR: Unclosed string literal");
                } else {
                    advance();  
                }
                tokens.add(new Token(TokenType.STRING_LITERALS, str.toString()));
            } else if (current == '\''){
                advance();
                if (position >= sourceCode.length() || sourceCode.charAt(position) == '\''){
                    System.err.println("ERROR: Empty Character literal");
                } else {
                    char charValue = sourceCode.charAt(position);
                    advance();
                    if (position <  sourceCode.length()&& sourceCode.charAt(position) == '\'') {
                        tokens.add(new Token(TokenType.CHAR_LITERALS, Character.toString(charValue)));
                        advance();
                    } else {
                        System.err.println("ERROR: Unclosed Character literal");
                    }
                }
            } else  if (current == '"'){
                StringBuilder stringLiteral = new StringBuilder();
                advance();

                while (current != '"' && current != '\0'){
                    stringLiteral.append(current);
                    advance();
                }

                if (current == '"'){
                    advance();
                    tokens.add(new Token(TokenType.STRING_LITERALS, stringLiteral.toString()));
                } else {
                    System.err.println("ERROR: Unterminated string literal");
                }
            } else if (current == ';'){
                tokens.add(new Token(TokenType.SEMICOLON, ";"));
                advance();
            } else {
                switch (current) {

                    case '+':
                        if (peek() == '='){
                            tokens.add(new Token(TokenType.PLUS_EQUALS, "+="));
                            advance();
                        } else if (peek() == '+'){
                            tokens.add(new Token(TokenType.INCREMENT, "++"));
                            advance();
                        } else {
                            tokens.add(new Token(TokenType.PLUS, "+"));
                        }
                        break;

                    case '-':
                        if (peek() == '='){
                            tokens.add(new Token(TokenType.MINUS_EQUALS, "-="));
                            advance();
                        } else if (peek() == '-'){
                            tokens.add(new Token(TokenType.DECREMENT, "--"));
                            advance();
                        } else {
                            tokens.add(new Token(TokenType.MINUS, "-"));
                        }
                        break;

                    case '=':
                        if (peek() == '='){
                            tokens.add(new Token(TokenType.EQUALS, "=="));
                            advance();
                        } else {
                            tokens.add(new Token(TokenType.ASSIGN, "="));
                        }
                        break;

                    case '!':
                        if (peek() == '='){
                            tokens.add(new Token(TokenType.NOT_EQUALS, "!="));
                            advance();
                        } else {
                            tokens.add(new Token(TokenType.NOT, "!"));
                        }
                        break;

                    case '&':
                        if (peek() == '&'){
                            tokens.add(new Token(TokenType.AND, "&&"));
                            advance();
                        } else {
                            tokens.add(new Token(TokenType.BITWISE_AND, "&"));
                        }
                        break;

                    case '|':
                        if (peek() == '|'){
                            tokens.add(new Token(TokenType.OR, "||"));
                            advance();
                        } else {
                            tokens.add(new Token(TokenType.BITWISE_OR, "|"));
                        }
                        break;

                    case '*':
                        if (peek() == '='){
                            tokens.add(new Token(TokenType.MULT_EQUALS, "*="));
                            advance();
                        } else {
                            tokens.add(new Token(TokenType.MULTIPLY, "*"));
                        }
                        break;

                    case '/':
                        if (peek() == '='){
                            tokens.add(new Token(TokenType.DIV_EQUALS, "/="));
                            advance();
                        } else if (peek() == '/'){
                            while (position < sourceCode.length() && sourceCode.charAt(position) != '\n'){
                                advance();
                            }
                            tokens.add(new Token(TokenType.DIVIDE, "/"));
                        } else if (peek() == '*') {
                            advance();
                            boolean closed = false;
                            while (position < sourceCode.length()){
                                if (sourceCode.charAt(position) == '*' && peek() == '/') {
                                    advance();
                                    advance();
                                    closed = true;
                                    break;
                                }
                                advance();
                            }
                            if(!closed){
                                System.err.println("ERROR: Unclosed multi-line comment");
                            }
                        } else {
                            tokens.add(new Token(TokenType.DIVIDE, "/"));
                        }
                        break;

                    //less than
                    case '<':
                        if (peek() == '='){
                            tokens.add(new Token(TokenType.LESS_THAN_EQUALS, "<="));
                            advance();
                        } else {
                            tokens.add(new Token(TokenType.LESS_THAN, "<"));
                        }
                        break;
                    //greater than
                    case '>':
                        tokens.add(peek() == '=' ? new Token(TokenType.GREATER_THAN_EQUALS, ">=") : new Token(TokenType.GREATER_THAN, ">"));
                        if (peek() == '=') advance();
                        break;

                    case '%':
                        tokens.add(new Token(TokenType.MOD, "%"));
                        break;

                    //parenthesis & brackets
                    case '(':
                        tokens.add(new Token(TokenType.LEFT_PAREN, "("));
                        break;
                    case ')':
                        tokens.add(new Token(TokenType.RIGHT_PAREN, ")"));
                        break;
                    case '{':
                        tokens.add(new Token(TokenType.LEFT_BRACE, "{"));
                        break;
                    case '}':
                        tokens.add(new Token(TokenType.RIGHT_BRACE, "}"));
                        break;
                    case '[':
                        tokens.add(new Token(TokenType.LEFT_BRACKET, "["));
                        break;
                    case ']':
                        tokens.add(new Token(TokenType.RIGHT_BRACKET, "]"));
                        break;
                    default:
                        tokens.add(new Token(TokenType.UNKNOWN, Character.toString(current)));
                }
                advance();
            }
        }
    }

    public static void main(String[] args) {
        String sourceCode = " String s = \"GGWEPH21aha Hello World\"; 'C'; 719ud2n1; int x = 42;\nfloat y = 3.14;\nchar c = 'A';\nif (x < y) { return; }";

        Lexer lexer = new Lexer(sourceCode);
        lexer.tokenize();
    
        // Print tokens
        for (Token token : lexer.tokens) {
            System.out.println(token);
        }
    }
}
