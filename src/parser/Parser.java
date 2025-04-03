package parser;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;
import parser.exprs.*;
import parser.stmts.*;

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

    public Token consume(TokenType expected) {
        return consume(expected, "Expected " + expected + " but found " + peek().getType());
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

    private static final Set<TokenType> TYPE_TOKENS = EnumSet.of(
    TokenType.INT, TokenType.FLOAT, TokenType.BOOLEAN, TokenType.CHAR
    );

    private boolean isType(Token token) {
        return TYPE_TOKENS.contains(token.getType());
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

        if (peek().getType() == TokenType.EOF) {
            throw new RuntimeException("Unclosed block: Expected '}' but reached end of file");
        }
        
        consume(TokenType.LEFT_BRACE, "Expected '{' to begin block");

        while (peek() != null && peek().getType() != TokenType.RIGHT_BRACE) {
            if (peek().getType() == TokenType.EOF) {
                throw new RuntimeException("Unclosed block: Expected '{' but reached end of file");
            }
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

        if (!(isType(type) || type.getType() == TokenType.VAR)) {
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
                throw new RuntimeException("Invalid syntax: Array dimensions cannot be specified in variable declaration, only during initialization");
            }
        }
        Token name = consume(TokenType.IDENTIFIER, "Expected identifier after 'var'");
       
        Expr initializer = null;
        if (peek().getType() == TokenType.ASSIGN){
            consume(TokenType.ASSIGN, "Expected '=' after variable name");
           
            if (peek().getType() == TokenType.SEMICOLON) {
                throw new RuntimeException("Missing value in variable assignment");
            }
    
            if (isArray){
                if (peek().getType() == TokenType.NEW){
                    initializer = parseNewArray();
                } else if (peek().getType() == TokenType.LEFT_BRACE){
                    initializer = parseArrayLiteral(dimensions, 0);
                } else {
                    throw new RuntimeException("Array initialization must use 'new' keyword or array literal");
                }
            } else {
                initializer = parseExpression();
            }
        }
        
        // ensure semicolon is consumed for all variable declarations
        consume(TokenType.SEMICOLON, "Expected ';' after variable declaration");
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
                // Prevent multiple consecutive commas
                if (peek().getType() == TokenType.COMMA) {
                    throw new RuntimeException("Unexpected comma. Missing element before comma.");
                }
                
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
        boolean foundDimension = false;

        while(peek().getType() == TokenType.LEFT_BRACKET){
            foundDimension = true;
            consume(TokenType.LEFT_BRACKET, "Expected '[' after type");
            if (peek().getType() == TokenType.RIGHT_BRACKET) {
                consume(TokenType.RIGHT_BRACKET, "Expected ']' after '['");
                if (dimensions.isEmpty()) {
                    throw new RuntimeException("First dimension size must be specified in array creation");
                }
                dimensions.add(null);
            } else {
                dimensions.add(parseExpression());
                consume(TokenType.RIGHT_BRACKET, "Expected ']' after expression");
            }
        }
        if (!foundDimension) {
            throw new RuntimeException("Array creation requires at least one dimension");
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

        switch(curr.getType()){
            case IDENTIFIER:
                consume(TokenType.IDENTIFIER, "Expected Identifier/Variable");
                return new VariableExpr(curr.getValue());
            
            case NUMBER_LITERALS:
                consume(TokenType.NUMBER_LITERALS, "Expected number");
                return new LiteralExpr(Double.parseDouble(curr.getValue()));

            case FLOAT_LITERALS:
                consume(TokenType.FLOAT_LITERALS, "Expected float");
                return new LiteralExpr(Double.parseDouble(curr.getValue())); 

            case CHAR_LITERALS:
                consume(TokenType.CHAR_LITERALS, "Expected character literal");
                String value = curr.getValue();
                if (value.length() != 1) {
                    throw new RuntimeException("Invalid character literal: " + value);
                }
                return new LiteralExpr(value.charAt(0));

            case STRING_LITERALS:
                consume(TokenType.STRING_LITERALS, "Expected string literal");
                return new LiteralExpr(curr.getValue());

            case BOOLEAN_LITERALS:
                consume(TokenType.BOOLEAN_LITERALS, "Expected boolean literal");
                return new LiteralExpr(Boolean.parseBoolean(curr.getValue()));
            
            case LEFT_PAREN:
                return parseGrouping();

            default:
                throw new RuntimeException("Unexpected token: '" + curr.getValue() + "' of type " + curr.getType() + ". Expected a variable, literal, or expression.");
        }
    }

    public Expr parseGrouping(){
        consume(TokenType.LEFT_PAREN, "Expected '(' at start of grouping.");
        Expr inner = parseExpression();
        consume(TokenType.RIGHT_PAREN, "Expected ')' to close grouping.");
        return new GroupingExpr(inner);
    }

    public static void main(String[] args) {
        String sourceCode = "boolean b = (x > y) && (y < z);";
        Lexer lexer = new Lexer(sourceCode);
        lexer.tokenize();

        List<Token> tokens = lexer.getTokens();
        System.out.println(tokens);
        Parser parser = new Parser(tokens);
        parser.parseStatement();
    }
}