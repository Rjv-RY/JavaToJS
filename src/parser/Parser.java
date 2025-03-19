import java.util.ArrayList;
import java.util.List;

import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;

class Parser{
    private List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    public Token peek(){
        if (current >= tokens.size()) {
            return new Token(TokenType.EOF, "");
        }
        return tokens.get(current);
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }
    
    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }
    
    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean match(TokenType expected) {
        if (peek().getType() == expected) {
            advance();  // Consume the token
            return true;
        }
        return false;
    }

    public Token consume(TokenType expected, String errorMessage){
        if (peek().getType() == TokenType.EOF) {
            throw new RuntimeException("Unexpected end of file. " + errorMessage);
        }
        if (expected == peek().getType()){
            Token curr = tokens.get(current);
            if (current < tokens.size()){
                current++;
            }
            return curr;
        } else {
            throw new RuntimeException(errorMessage);
        }
    }

    public LiteralExpr parseLiteral(){
        Token curr = peek();
        if (curr.getType() == TokenType.NUMBER_LITERALS){
            double num = Double.parseDouble(curr.getValue());
            consume(TokenType.NUMBER_LITERALS, "Not a Number");
            return new LiteralExpr(num);
        } else if (curr.getType() == TokenType.STRING_LITERALS){
            consume(TokenType.STRING_LITERALS, "Not a String");
            return new LiteralExpr((curr.getValue()));
        } else if (curr.getType() == TokenType.CHAR_LITERALS){
            char chr = curr.getValue().charAt(0);
            consume(TokenType.CHAR_LITERALS, "Not a Char");
            return new LiteralExpr(chr);
        }
        return null;
    }

// 1️⃣ Parentheses (highest precedence) → (expr)
// 2️⃣ Unary operators → +, -, !
// 3️⃣ Multiplication & Division → *, /
// 4️⃣ Addition & Subtraction → +, -
// 5️⃣ Comparison Operators → <, >, <=, >=, ==, !=
// 6️⃣ Logical AND → &&
// 7️⃣ Logical OR → ||
// 8️⃣ Assignment (lowest precedence) → =

    public Expr parseExpression(){
        return parseAssignment();
    }

    private boolean isType(Token token) {
        return token.getType() == TokenType.INT ||
               token.getType() == TokenType.FLOAT ||
               token.getType() == TokenType.BOOLEAN ||
               token.getType() == TokenType.CHAR;
    }

    public Stmt parseStatement(){
        Token curr = peek();

        if (curr.getType() == TokenType.IF){
            return parseIf();
        } else if (curr.getType() == TokenType.WHILE){
            return parseWhile();
        } else if (curr.getType() == TokenType.PRINT){
            return parsePrint();
        } else if (curr.getType() == TokenType.VAR || isType(curr)){
            return parseVarDecleration();
        } else if (curr.getType() == TokenType.LEFT_BRACE){
            return new BlockStmt(parseBlock());
        }else {
            return parseExpressionStatement();
        }
    }

    //parsing block statements
    public List<Stmt> parseBlock() {
    List<Stmt> statements = new ArrayList<>();
    
    consume(TokenType.LEFT_BRACE, "Expected '{' to begin block");

    while (peek() != null && peek().getType() != TokenType.RIGHT_BRACE) {
        statements.add(parseStatement());
    }

    consume(TokenType.RIGHT_BRACE, "Expected '}' to close block");
    return statements;
    }

    //parsing If
    public Stmt parseIf(){
        consume(TokenType.IF, "Expected 'If' keyword");
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'if'");
        
        Expr condition = parseExpression();

        consume(TokenType.RIGHT_PAREN, "Expected ')' after '(' and 'if' keyword" );
    
        Stmt thenBranch;
        if (peek().getType() == TokenType.LEFT_BRACE){
            thenBranch = new BlockStmt(parseBlock());
        } else {
            thenBranch = parseStatement();
        }

        Stmt elseBranch = null;
        if (peek() != null && peek().getType() == TokenType.ELSE){
            consume(TokenType.ELSE, "Expected 'else' keyword");
            if (peek().getType() == TokenType.LEFT_BRACE){
                elseBranch = new BlockStmt(parseBlock());
            } else {
                elseBranch = parseStatement();
            }
        }

        return new IfStmt(condition, thenBranch, elseBranch);
    }
    
    //parsing while
    public Stmt parseWhile(){
        consume(TokenType.WHILE, "Expected 'while' keyword");
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'while'");
        
        Expr condition = parseExpression();
        
        consume(TokenType.RIGHT_PAREN, "Expected ')' after '(' and 'while' keyword" );
        
        Stmt body;
        if (peek().getType() == TokenType.LEFT_BRACE){
            body = new BlockStmt(parseBlock());
        } else {
            body = parseStatement();
        }
        
        return new WhileStmt(condition, body);
    }

    //parsing print statement
    public Stmt parsePrint() {
        consume(peek().getType(), "Expected 'print' or 'println' keyword");
    
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'print' or 'println'");
        Expr expr = parseExpression();
        consume(TokenType.RIGHT_PAREN, "Expected ')' after expression");
        consume(TokenType.SEMICOLON, "Expected ';' after print statement");
    
        return new PrintStmt(expr);
    }

    public Stmt parseVarDecleration(){
        Token type = peek();
    
        // Check if it's a valid type keyword or 'var'
        if (!(type.getType() == TokenType.VAR ||
              type.getType() == TokenType.INT ||
              type.getType() == TokenType.FLOAT ||
              type.getType() == TokenType.BOOLEAN ||
              type.getType() == TokenType.CHAR)) {
            throw new RuntimeException("Expected type keyword (int, float, boolean, char, var) in variable declaration.");
        }

        advance();

        List<Integer> dimensions = new ArrayList<>();
        boolean isArray = false;
        while (peek().getType() == TokenType.LEFT_BRACKET){
            consume(TokenType.LEFT_BRACKET, "Expected '[' in array declaration");
            isArray = true;

            if (peek().getType() == TokenType.RIGHT_BRACKET) {
                consume(TokenType.RIGHT_BRACKET, "Expected ']' after '['");
                dimensions.add(-1);
            } else {
                if (peek().getType() == TokenType.NUMBER_LITERALS) { 
                    dimensions.add(Integer.parseInt(consume(TokenType.NUMBER_LITERALS, "Expected array size inside '['").getValue()));
                    consume(TokenType.RIGHT_BRACKET, "Expected ']' after array size");
                } else {
                    throw new RuntimeException("Expected array size inside '['");
                }
            }
        }
        Token name = consume(TokenType.IDENTIFIER, "Expected identifier after 'var'");
        
        Expr initializer = null;
        if (peek().getType() == TokenType.ASSIGN){
            consume(TokenType.ASSIGN, "Expected '=' after variable name");
            
            if (peek().getType() == TokenType.SEMICOLON) {
                throw new RuntimeException("Missing value in variable assignment");
            }

            if (type.getType() == TokenType.BOOLEAN) { 
                initializer = parseExpression();

                if (!(initializer instanceof LiteralExpr && (((LiteralExpr) initializer).getValue().equals("true") ||((LiteralExpr) initializer).getValue().equals("false")))) {
                    throw new RuntimeException("Boolean variables can only be assigned 'true' or 'false'");
                }
            } else {
                if (isArray && peek().getType() == TokenType.LEFT_BRACE){
                    initializer = parseArrayLiteral(dimensions, 0);
                } else if (peek().getType() == TokenType.NEW){
                    initializer = parseNewArray();
                } else {
                    initializer = parseExpression();
                }
            }
            
        }
        consume(TokenType.SEMICOLON, "Expected ';' after variable decleration");
        return new VarStmt(type, name, initializer, isArray);
    }

    //parsing arrays
    private Expr parseArrayLiteral(List<Integer> dimensions, int depth){
        consume(TokenType.LEFT_BRACE, "Expected '{' for array literal");
        List<Expr> elements = new ArrayList<>();

        int expectedSize = dimensions.size() > depth ? dimensions.get(depth) : -1;
        boolean dynamicSize = (expectedSize == -1);

        if (peek().getType() == TokenType.RIGHT_BRACE) {
            consume(TokenType.RIGHT_BRACE, "Expected '}' at the end of array literal");
            return new ArrayLiteralExpr(elements);
        }
    
        while (true) {
            //handle eof
            if (peek().getType() == TokenType.EOF || peek().getType() == TokenType.SEMICOLON) {
                throw new RuntimeException("Missing closing '}' in array literal");
            }

            //handle left brace
            if (peek().getType() == TokenType.LEFT_BRACE) {
                if (depth + 1 >= dimensions.size()  && !dynamicSize) {
                    throw new RuntimeException("Unexpected nested array beyond declared dimensions.");
                }
                elements.add(parseArrayLiteral(dimensions, depth + 1));
            } else {
                elements.add(parseExpression());
            }

            //for commas
            if (peek().getType() == TokenType.COMMA) {
                consume(TokenType.COMMA, "Expected ',' between array elements");
                // chek trail comma
                if (peek().getType() == TokenType.RIGHT_BRACE) {
                    break;
                }
            } else if (peek().getType() == TokenType.RIGHT_BRACE) {
                break;
            } else {
                throw new RuntimeException("Expected ',' or '}' after array element, found: " + peek().getType());
            }
        }

        consume(TokenType.RIGHT_BRACE, "Expected '}' at the end of array literal");
        
        if (!dynamicSize){

            if (elements.size() > expectedSize) {
                throw new RuntimeException("Too many elements in array literal at depth " + depth + " (expected " + expectedSize + ", got " + elements.size() + ")");
            } else if (elements.size() < expectedSize) {
                throw new RuntimeException("Too few elements in array literal at depth " + depth + " (expected " + expectedSize + ", got " + elements.size() + ")");
            }
        }
        
        
        return new ArrayLiteralExpr(elements);
    }

    private Expr parseNewArray(){
        consume(TokenType.NEW, "Expected 'new' keyword for array allocation");
        Token type = consume(TokenType.INT, "Expected type after new");

        List<Expr> dimensions = new ArrayList<>();
        while(peek().getType() == TokenType.LEFT_BRACKET){
            consume(TokenType.LEFT_BRACKET, "Expected '[' after type");
            dimensions.add(parseExpression());
            consume(TokenType.RIGHT_BRACKET, "Expected '[' after type");
        }
        return new NewArrayExpr(type, dimensions);
    
    }

    public Stmt parseExpressionStatement(){
        Expr expr = parseExpression();
        consume(TokenType.SEMICOLON, "Expected ';' statement closes");
        return new ExprStmt(expr);
    }
    
    public Expr parseAssignment(){
        Token curr = peek();
        if (curr.getType() == TokenType.IDENTIFIER){
            Token next = tokens.get(current + 1);
            if (next != null && next.getType() == TokenType.ASSIGN){
                Token name = consume(TokenType.IDENTIFIER, "Expected variable/identifier");
                Token assign = consume(TokenType.ASSIGN, "Expected '+' after variable name");
                Expr right = parseExpression();
                return new AssignmentExpr(name, assign, right);   
            }
        }
        return parseLogical();
    }

    private Expr parseLogical(){
        Expr expr = parseLogicalAND();

        while (peek() != null && peek().getType() == TokenType.OR){
            Token operator = consume(peek().getType(), "Expected '&&' or '||'");
            Expr right = parseComparison();
            expr = new BinaryExpr(expr, operator, right);
        }
        return expr;
    }

    private Expr parseLogicalAND(){
        Expr expr = parseComparison();

        while (peek() != null && peek().getType() == TokenType.AND){
            Token operator = consume(TokenType.AND, "Expected '&&'");
            Expr right = parseComparison();
            expr = new BinaryExpr(expr, operator, right);  
        }
        return expr;
    }

    private boolean isComparisonOpr(TokenType type){
        return type == TokenType.LESS_THAN || type == TokenType.GREATER_THAN ||
        type == TokenType.LESS_THAN_EQUALS || type == TokenType.GREATER_THAN_EQUALS ||
        type == TokenType.EQUALS || type == TokenType.NOT_EQUALS;
    }

    private Expr parseComparison(){
        Expr expr = parseTerm();

        while (peek() != null && isComparisonOpr(peek().getType())){
            Token operator = consume(peek().getType(), "Expected '<' or '<=' or '>' or '>='");
            Expr right = parseTerm();
            expr = new BinaryExpr(expr, operator, right);
        }
        return expr;
    }

    private Expr parseTerm(){
        Expr expr = parseFactor();

        while (peek() != null && (peek().getType() == TokenType.PLUS || peek().getType() == TokenType.MINUS)){
            Token operator = consume(peek().getType(), "Expected '+' or '-'");
            Expr right = parseFactor();
            expr = new BinaryExpr(expr, operator, right);
        }
        return expr;
    }

    private Expr parseFactor(){
        Token curr = peek();
        if (curr.getType() == TokenType.MINUS || curr.getType() == TokenType.PLUS || curr.getType() == TokenType.NOT){
            Token operator = consume(curr.getType(), "Expected unary operator '-' or '+' or logical '!'");
            Expr right = parseFactor();
            return new UnaryExpr(operator, right);
        }

        Expr expr = parsePrimary();

        while(peek() != null && (peek().getType() == TokenType.MULTIPLY|| peek().getType() == TokenType.DIVIDE)){
            Token operator = consume(peek().getType(), "Expected '*' or '/'");
            Expr right = parsePrimary();
            expr = new BinaryExpr(expr, operator, right);
        }
        return expr;
    }

    private Expr parsePrimary(){
        Token curr = peek();

        if(curr.getType() == TokenType.IDENTIFIER){
            consume(TokenType.IDENTIFIER, "Expected Identifier/Variable");
            return new VariableExpr(curr.getValue());
        }

        if (curr.getType() == TokenType.NUMBER_LITERALS){
            consume(TokenType.NUMBER_LITERALS, "Expected number");
            return new LiteralExpr(Double.parseDouble(curr.getValue()));
        }
        
        if (curr.getType() == TokenType.FLOAT_LITERALS) {
            consume(TokenType.FLOAT_LITERALS, "Expected float");
            return new LiteralExpr(Double.parseDouble(curr.getValue()));
        }

        if (curr.getType() == TokenType.CHAR_LITERALS) {
            consume(TokenType.CHAR_LITERALS, "Expected character literal");
            String value = curr.getValue();
            if (value.length() != 1) {
                throw new RuntimeException("Invalid character literal: " + value);
            }
            return new LiteralExpr(value.charAt(0));  // Convert to char
        }

        if (curr.getType() == TokenType.STRING_LITERALS) {
            consume(TokenType.STRING_LITERALS, "Expected string literal");
            return new LiteralExpr(curr.getValue());  // Store as a String
        }

        if (curr.getType() == TokenType.BOOLEAN_LITERALS) {
            consume(TokenType.BOOLEAN_LITERALS, "Expected boolean literal");
            return new LiteralExpr(Boolean.parseBoolean(curr.getValue()));  // Convert to boolean
        }

        if (curr.getType() == TokenType.LEFT_PAREN){
            return parseGrouping();
        }

        throw new RuntimeException("Unexpected token: " + curr.getValue() + ", " + curr.getType());
    }

    public Expr parseGrouping(){
        consume(TokenType.LEFT_PAREN, "Expected '(' at start of grouping.");
        Expr inner = parseExpression();
        consume(TokenType.RIGHT_PAREN, "Expected ')' to close grouping.");
        return new GroupingExpr(inner);
    }

    public static void main(String[] args) {
        String sourceCode = "boolean flag = 1;";

        Lexer lexer = new Lexer(sourceCode);
        lexer.tokenize();
            
        List<Token> tokens = lexer.getTokens();// Get the token list
        System.out.println(tokens);
        Parser parser = new Parser(tokens);     // Pass to parser
        parser.parseStatement();   
    }

    //this is something of a nightmare as of yet. 
    //parseExpression calls parseLogical calls parseLogicalAnd 
    //calls parseComparison calls parseTerm calls parseFactor 
    //calls parsePrimary calls parseGrouping and also parseLiteral
}