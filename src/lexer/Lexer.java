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
    private List<Token> tokens = new ArrayList<>();
    
    private static final Map<String, TokenType> keywordMap = new HashMap<>();
    
    public List<Token> getTokens() {
    return tokens;
}

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
        keywordMap.put("println", TokenType.PRINTLN);
        keywordMap.put("var", TokenType.VAR);
        keywordMap.put("new", TokenType.NEW);
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

    public void tokenize() {
        while (position < sourceCode.length()) {
            scanToken();
        }
    }

    private void scanToken() {
        char current = sourceCode.charAt(position);
    
        if (Character.isWhitespace(current)) {
            advance();
        } else if (Character.isDigit(current)) {
            scanNumber();
        } else if (Character.isLetter(current)) {
            scanIdentifier();
        } else if (current == '"') {
            scanString();
        } else if (current == '\'') {
            scanCharLiteral();
        } else if (current == '/') {
            // Special handling for comments
            if (position + 1 < sourceCode.length()) {
                char next = sourceCode.charAt(position + 1);
                
                if (next == '/') {
                    // Single line comment
                    advance(); // Skip /
                    advance(); // Skip /
                    while (position < sourceCode.length() && sourceCode.charAt(position) != '\n') {
                        advance();
                    }
                    // No need to consume the newline - it will be handled as whitespace in the next iteration
                } else if (next == '*') {
                    // Multiline comment
                    advance(); // Skip /
                    advance(); // Skip *
                    
                    boolean closed = false;
                    while (position + 1 < sourceCode.length()) {
                        if (sourceCode.charAt(position) == '*' && 
                            position + 1 < sourceCode.length() && 
                            sourceCode.charAt(position + 1) == '/') {
                            advance(); // Skip *
                            advance(); // Skip /
                            closed = true;
                            break;
                        }
                        advance();
                    }
                    
                    if (!closed) {
                        System.err.println("ERROR: Unclosed multi-line comment at line " + lineNumber);
                    }
                } else {
                    // Not a comment, treat as a normal symbol
                    scanSymbol();
                }
            } else {
                // Just a single / at the end of file
                scanSymbol();
            }
        } else {
            scanSymbol();
        }
    }

    private void scanNumber() {
        StringBuilder number = new StringBuilder();
        boolean floatPoint = false;
        char current = sourceCode.charAt(position);
    
        while (position < sourceCode.length() && (Character.isDigit(current) || (current == '.' && !floatPoint))) {
            if (current == '.') {
                floatPoint = true;
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
    }

    private void scanIdentifier() {
        StringBuilder identifier = new StringBuilder();
        char current = sourceCode.charAt(position);
    
        while (position < sourceCode.length() && (Character.isLetterOrDigit(current) || current == '_')) {
            identifier.append(current);
            advance();
            if (position < sourceCode.length()) {
                current = sourceCode.charAt(position);
            }
        }
    
        String lexeme = identifier.toString();
        TokenType type = keywordMap.getOrDefault(lexeme, TokenType.IDENTIFIER);
        tokens.add(new Token(type, lexeme));
    }

    private void scanString() {
        advance();
        StringBuilder str = new StringBuilder();
    
        while (position < sourceCode.length() && sourceCode.charAt(position) != '"') {
            if (sourceCode.charAt(position) == '\\') {
                advance();
                if (position < sourceCode.length()) {
                    char escapeChar = sourceCode.charAt(position);
                    switch (escapeChar) {
                        case 'n': str.append('\n'); break;
                        case 't': str.append('\t'); break;
                        case 'r': str.append('\r'); break;
                        case '"': str.append('"'); break;
                        case '\\': str.append('\\'); break;
                        default:
                            System.err.println("ERROR: Invalid escape sequence \\" + escapeChar);
                            break;
                    }
                }
            } else {
                str.append(sourceCode.charAt(position));
            }
            advance();
        }
    
        if (position >= sourceCode.length()) {
            System.err.println("ERROR: Unclosed string literal");
        } else {
            advance(); // Consume closing "
        }
        tokens.add(new Token(TokenType.STRING_LITERALS, str.toString()));
    }

    private void scanCharLiteral() {
        advance();
        StringBuilder charLiteral = new StringBuilder();
    
        if (position < sourceCode.length() && sourceCode.charAt(position) == '\\') {
            advance();
            if (position < sourceCode.length()) {
                char escapeChar = sourceCode.charAt(position);
                switch (escapeChar) {
                    case 'n': charLiteral.append('\n'); break;
                    case 't': charLiteral.append('\t'); break;
                    case 'r': charLiteral.append('\r'); break;
                    case '\'': charLiteral.append('\''); break;
                    case '\\': charLiteral.append('\\'); break;
                    default:
                        System.err.println("ERROR: Invalid escape sequence \\" + escapeChar);
                        break;
                }
            }
        } else if (position < sourceCode.length()) {
            charLiteral.append(sourceCode.charAt(position));
        } else {
            System.err.println("ERROR: Empty Character literal");
        }
    
        advance();
        if (position < sourceCode.length() && sourceCode.charAt(position) == '\'') {
            if (charLiteral.length() == 0) {
                System.err.println("ERROR: Empty character literal");
            }
            tokens.add(new Token(TokenType.CHAR_LITERALS, charLiteral.toString()));
            advance();
        } else {
            System.err.println("ERROR: Unclosed Character literal");
        }
    }

    private void scanSymbol() {
    char current = sourceCode.charAt(position);
    if (current == ';'){
        tokens.add(new Token(TokenType.SEMICOLON, ";"));
        advance();
    } else {
        switch (current) {

            case '+':
                if (peek() == '='){
                    tokens.add(new Token(TokenType.PLUS_EQUALS, "+="));
                    advance();
                    advance();
                } else if (peek() == '+'){
                    tokens.add(new Token(TokenType.INCREMENT, "++"));
                    advance();
                    advance();
                } else {
                    tokens.add(new Token(TokenType.PLUS, "+"));
                    advance();
                }
                break;

            case '-':
                if (peek() == '='){
                    tokens.add(new Token(TokenType.MINUS_EQUALS, "-="));
                    advance();
                    advance();
                } else if (peek() == '-'){
                    tokens.add(new Token(TokenType.DECREMENT, "--"));
                    advance();
                    advance();
                } else {
                    tokens.add(new Token(TokenType.MINUS, "-"));
                    advance();
                }
                break;

            case '=':
                if (peek() == '='){
                    tokens.add(new Token(TokenType.EQUALS, "=="));
                    advance();
                    advance();
                } else {
                    tokens.add(new Token(TokenType.ASSIGN, "="));
                    advance();
                }
                break;

            case '!':
                if (peek() == '='){
                    tokens.add(new Token(TokenType.NOT_EQUALS, "!="));
                    advance();
                    advance();
                } else {
                    tokens.add(new Token(TokenType.NOT, "!"));
                    advance();
                }
                break;

            case '&':
                if (peek() == '&'){
                    tokens.add(new Token(TokenType.AND, "&&"));
                    advance();
                    advance();
                } else {
                    tokens.add(new Token(TokenType.BITWISE_AND, "&"));
                    advance();
                }
                break;

            case '|':
                if (peek() == '|'){
                    tokens.add(new Token(TokenType.OR, "||"));
                    advance();
                    advance();
                } else {
                    tokens.add(new Token(TokenType.BITWISE_OR, "|"));
                    advance();
                }
                break;

            case '*':
                if (peek() == '='){
                    tokens.add(new Token(TokenType.MULT_EQUALS, "*="));
                    advance();
                    advance();
                } else {
                    tokens.add(new Token(TokenType.MULTIPLY, "*"));
                    advance();
                }
                break;

            case '/':
                if (peek() == '=') {
                    tokens.add(new Token(TokenType.DIV_EQUALS, "/="));
                    advance();
                    advance();
                } else {
                    tokens.add(new Token(TokenType.DIVIDE, "/"));
                    advance();
                }
                break;

            //less than
            case '<':
                if (peek() == '='){
                    tokens.add(new Token(TokenType.LESS_THAN_EQUALS, "<="));
                    advance();
                    advance();
                } else {
                    tokens.add(new Token(TokenType.LESS_THAN, "<"));
                    advance();
                }
                break;
            //greater than
            case '>':
                if (peek() == '=') {
                    tokens.add(new Token(TokenType.GREATER_THAN_EQUALS, ">="));
                    advance();
                    advance();
                } else {
                    tokens.add(new Token(TokenType.GREATER_THAN, ">"));
                    advance();
                }
                break;

            case ',':
                tokens.add(new Token(TokenType.COMMA, ","));
                advance();
                break;

            case '%':
                tokens.add(new Token(TokenType.MOD, "%"));
                advance();
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

    public static void main(String[] args) {

        String sourceCode = "int[] arr = {1, 2, 3, 4};\n"
             + "int[][] matrix = new int[3][3];\n"
             + "arr[2] = 42;";

        Lexer lexer = new Lexer(sourceCode);
        lexer.tokenize();
    
        // Print tokens
        for (Token token : lexer.tokens) {
            System.out.println(token);
        }
    }
}
