package transpiler;

import lexer.TokenType;
import parser.*;
import parser.exprs.*;
import parser.stmts.*;

public class SemanticAnalyzer{
    private SymbolTable symbolTable;

    public SemanticAnalyzer() {
        this.symbolTable = new SymbolTable();
    }

    //for statements(stmt)
    public void analyze(Stmt stmt) {
        if (stmt instanceof VarStmt varStmt){
            analyzeVarStmt(varStmt);
        } else if (stmt instanceof  IfStmt ifStmt){
            analyzeIfStmt(ifStmt);
        } else if (stmt instanceof WhileStmt whileStmt){
            analyzeWhileStmt(whileStmt);
        } else if (stmt instanceof  BlockStmt blockStmt){
            analyzeBlockStmt(blockStmt);
        } else if (stmt instanceof PrintStmt printStmt){
            analyzePrintStmt(printStmt);
        } else if (stmt instanceof  ExprStmt exprStmt){
            analyzeExprStmt(exprStmt);
        }
    }

    // for expressions (exprs)
    private String analyzeExpression(Expr expr) {
        if (expr instanceof LiteralExpr literalExpr) {
            return inferLiteralType(literalExpr);
        } else if (expr instanceof VariableExpr variableExpr) {
            String varName = variableExpr.getVar();
            if (!symbolTable.isDeclared(varName)) {
                throw new RuntimeException("Undeclared variable: " + varName);
            }
            return symbolTable.getVariableType(varName); // Retrieve stored type
        } else if (expr instanceof AssignmentExpr assignmentExpr) {
            return analyzeAssignmentExpr(assignmentExpr); 
        } else if (expr instanceof BinaryExpr binaryExpr) {
            return analyzeBinaryExpr(binaryExpr); // Call binary expression analysis
        } else if (expr instanceof UnaryExpr unaryExpr) {
            return analyzeUnaryExpr(unaryExpr);
        } else if (expr instanceof ArrayLiteralExpr arrayLiteralExpr) {
            return analyzeArrayLiteralExpr(arrayLiteralExpr);
        } else if (expr instanceof NewArrayExpr newArrayExpr) {
            return analyzeNewArrayExpr(newArrayExpr);
        } else if (expr instanceof GroupingExpr groupingExpr){
            return analyzeGroupingExpr(groupingExpr);
        } else if (expr instanceof PostfixExpr postfixExpr){
            return analyzePostfixExpr(postfixExpr);
        }
        throw new RuntimeException("Unsupported expression type");
    }

//STMTS AHEAD
//!!STATEMENTS COUNTY!!

    public void analyzeBlockStmt(BlockStmt blockStmt){
        symbolTable.enterScope();

        for (Stmt stmt : blockStmt.getStatements()){
            analyze(stmt);
        }

        symbolTable.exitScope();
    }

    private boolean isValidConditionType(String type) {
        switch (type) {
            case "boolean":
                return true; // already a boolean, no conversion
            case "int":
                return true;
            case "float":
                return true; // numeric types (0 = false, anything else = true)
            case "string":
                return true; // string types (empty = false, non-empty = true)
            default:
                return false; // other types, invalid
        }
    }

    //needs testing, THROUGHLY
    private void analyzeVarStmt(VarStmt stmt){
        String varName = stmt.getName().getValue();
        String varType = stmt.getType().getValue();
        boolean isArray = stmt.isArray();

        String fullType = isArray ? varType + "[]" : varType;
        symbolTable.declareVariable(varName, fullType);

        if (stmt.getInitialzer() != null){
            Expr initializer = stmt.getInitialzer();
            String inferredType = analyzeExpression(initializer);

            if (initializer instanceof NewArrayExpr){
                // Handle array initialization with new
                if (!isArray) {
                    throw new RuntimeException("Cannot initialize non-array variable with array expression");
                }
                
                String arrayBaseType = ((NewArrayExpr)initializer).getType().getValue();
                if (!arrayBaseType.equals(varType)){
                    throw new RuntimeException("Array type mismatch: variable declared as " + varType + 
                                              "[] but initialized with " + arrayBaseType + "[]");
                }
            } else if (initializer instanceof ArrayLiteralExpr) {
                // handle array initialization with literal
                if (!isArray){
                    throw new RuntimeException("Cannot initialize non-array variable with array literal");
                }
                
                if (!inferredType.endsWith("[]")){
                    throw new RuntimeException("Expected array type but got " + inferredType);
                }
    
                String inferredBaseType = inferredType.substring(0, inferredType.length() - 2);
                if (!inferredBaseType.equals(varType)){
                    throw new RuntimeException("Array type mismatch: expected " + varType + 
                                              "[] but got " + inferredBaseType + "[]");
                }
            } else if (isArray) {
                throw new RuntimeException("Array variable must be initialized with array expression");
            } else if (!inferredType.equals(varType)) {
                throw new RuntimeException("Type mismatch: expected " + varType + " but got " + inferredType);
            }
        }
    }

    private String inferLiteralType(LiteralExpr expr){
        TokenType type = expr.getTokenType();

        switch (type) {
            case NUMBER_LITERALS:
                return "int";
            case FLOAT_LITERALS:
                return "float";
            case DOUBLE_LITERALS:
                return "double";
            case CHAR_LITERALS:
                return "char";
            case STRING_LITERALS:
                return "string";
            case BOOLEAN_LITERALS:
                return "boolean";
            default:
                return inferTypeFromValue(expr.getValue());
        }
    }

    private String inferTypeFromValue(Object value){
        if (value instanceof Double){
            return ((Double)value == Math.floor((Double)value)) ? "int" : "float";
        } else if (value instanceof Boolean){
            return "boolean";
        } else if (value instanceof Character){
            return "char";
        } else if (value instanceof String){
            return "string";
        }
        throw new RuntimeException("Unknown literal type: " + value.getClass());
    }

    private void analyzeIfStmt(IfStmt stmt){
        String conditionType = analyzeExpression(stmt.getCondition());
        try {
            coerceExpressionToBoolean(stmt.getCondition(), conditionType);
        } catch (RuntimeException e) {
            throw new RuntimeException("If statement condition error: " + e.getMessage());
        }
        symbolTable.enterScope(); 
        analyze(stmt.getThenBranch());

        symbolTable.exitScope();

        if (stmt.getElseBranch() != null) {
            symbolTable.enterScope();
            analyze(stmt.getElseBranch());
            symbolTable.exitScope();
        }
    }

    private void analyzeWhileStmt(WhileStmt stmt){
        String conditionType = analyzeExpression(stmt.getCondition());
        if (!isValidConditionType(conditionType)) {
            throw new RuntimeException("While statement condition must be coercible to boolean, got: " + conditionType);
        }

        try {
            coerceExpressionToBoolean(stmt.getCondition(), conditionType);
        } catch (RuntimeException e) {
            throw new RuntimeException("While statement condition error: " + e.getMessage());
        }

        symbolTable.enterScope();
        analyze(stmt.getBody());
        symbolTable.exitScope();
    }

    private void analyzeExprStmt(ExprStmt stmt){
        try {
            analyzeExpression(stmt.getExpr());
        } catch (Exception e) {
            throw new RuntimeException("Error in expression statement: " + e.getMessage());
        }
    }

    private void analyzePrintStmt(PrintStmt stmt){
        try {
            analyzeExpression(stmt.getExpr());
        } catch (Exception e) {
            throw new RuntimeException("Error in print statement: " + e.getMessage());
        }  
    }

    private String coerceExpressionToBoolean(Expr expr, String exprType){
        if (exprType.equals("boolean")){
            return exprType;
        }

        if(exprType.equals("int") || exprType.equals("float")){
            return "boolean";
        }

        if(exprType.equals("string")){
            return "boolean";
        }

        throw new RuntimeException("Cannot coerce type " + exprType + " to boolean");
    }


//EXPRS AHEAD
//!!EXPRESSIONS LAND!!


    private String analyzeUnaryExpr(UnaryExpr expr){
        String right = analyzeExpression(expr.getRight());
        String operator = expr.getOperator().getValue();

        switch (operator){
            case "-":
                if(!right.equals("int") && !right.equals("float")){
                    throw new RuntimeException("Unary '-' operator requires a numeric operand");
                }
                return right;
            
            case "!":
                if(!right.equals("boolean")){
                    throw new RuntimeException("Unary '!' operator requires a boolean operand");
                }
                return "boolean";

            case "++":
            case "--":
                if (!right.equals("int") && !right.equals("float")){
                    throw new RuntimeException("Prefix increment/decrement operator requires a numeric operand");
                }
                if (!(expr.getRight() instanceof VariableExpr)){
                    throw new RuntimeException("Prefix increment/decrement can only be applied to variables");
                }
                return right;

            default:
                throw new RuntimeException("Unsupported unary operator: " + operator);
        }
    }

    private String analyzePostfixExpr (PostfixExpr expr){
        String leftType = analyzeExpression(expr.getOperand());

        if (!(expr.getOperand() instanceof VariableExpr)){
            throw new RuntimeException("Postfix operator can only be applied to vars.");
        }

        String operator = expr.getOperator().getValue();

        switch (operator){
            case "++":
            case "--":
                if (!leftType.equals("int") && !leftType.equals("float")){
                    throw new RuntimeException("Postfix operator '" + operator + "' requires a numeric operand.");
                }

                return leftType;
            default:
                throw new RuntimeException("Unsupported postfix operator: " + operator);
        }        
    }

    private String analyzeGroupingExpr(GroupingExpr expr){
        try {
            return analyzeExpression(expr.getExpr());
        } catch (Exception e) {
            throw new RuntimeException("Error in grouping expression: " + e.getMessage()); 
        }
    }

    private String analyzeArrayLiteralExpr(ArrayLiteralExpr expr){
        String elementType = null;

        for (Expr element : expr.getElements()){
            String currentType = analyzeExpression(element);

            if (elementType == null){
                elementType = currentType;
            } else if (!elementType.equals(currentType)){
                throw new RuntimeException("Inconsistent types in array literal: " + elementType + " and " + currentType);
            }
        }
        return elementType + "[]";
    }

    private String analyzeNewArrayExpr(NewArrayExpr expr){
        String baseType = expr.getType().getValue();

        for (Expr dimension : expr.getDimensions()){
            if (dimension != null){
                String dimensionType = analyzeExpression(dimension);
                if (!dimensionType.equals("int")){
                    throw new RuntimeException("Array dimension must be an integer");
                }
            }
        }

        StringBuilder arrayType = new StringBuilder(baseType);
        for (int i  = 0; i < expr.getDimensions().size(); i++){
            arrayType.append("[]");
        }

        return arrayType.toString();
    }

    private String analyzeAssignmentExpr(AssignmentExpr expr){
        String varName = expr.getName().getValue();
        if (!symbolTable.isDeclared(varName)){
            throw new RuntimeException("Undeclared variable: " + varName);
        }
    
        String expectedType = symbolTable.getVariableType(varName);
        String inferredType = analyzeExpression(expr.getRight());
        
        //for array assignment
        if (expectedType.endsWith("[]")) {
            // array vars
            if (expr.getRight() instanceof NewArrayExpr) {
                // get basetype from array type (like "int" from "int[]")
                String expectedBaseType = expectedType.substring(0, expectedType.length() - 2);
                String inferredBaseType;
                
                // check inferredType also ends with "[]"
                if (inferredType.endsWith("[]")) {
                    inferredBaseType = inferredType.substring(0, inferredType.length() - 2);
                } else {
                    //no array notation in inferredType, use as base type
                    inferredBaseType = inferredType;
                }
                
                // base types match?
                if (!expectedBaseType.equals(inferredBaseType)) {
                    throw new RuntimeException("Array type mismatch: expected " + expectedType + " but got " + inferredBaseType + "[]");
                }
                return expectedType;
            } else if (expr.getRight() instanceof ArrayLiteralExpr) {
                // validation for array literals
                if (!inferredType.endsWith("[]")) {
                    throw new RuntimeException("Expected array type " + expectedType + " but got " + inferredType);
                }
                
                String inferredBaseType = inferredType.substring(0, inferredType.length() - 2);
                String expectedBaseType = expectedType.substring(0, expectedType.length() - 2);
                
                if (!expectedBaseType.equals(inferredBaseType)) {
                    throw new RuntimeException("Array type mismatch: expected " + expectedType + " but got " + inferredType);
                }
                return expectedType;
            }
        }
        if (!expectedType.equals(inferredType)) {
            throw new RuntimeException("Type mismatch: expected " + expectedType + " but got " + inferredType);
        }
        return expectedType;
    }

    private String analyzeBinaryExpr(BinaryExpr expr){
        String leftType = analyzeExpression(expr.getLeft());
        String rightType = analyzeExpression(expr.getRight());
        String operator = expr.getOperator().getValue();

        if (operator.equals("&&") || operator.equals("||")){
            if (!leftType.equals("boolean") || !rightType.equals("boolean")){
                throw new RuntimeException("Logical operators require boolean operands");
            }
            return "boolean";
        } else if (operator.matches("<|<=|>|>=")){
            if (!(leftType.equals("int") || leftType.equals("float")) || !(rightType.equals("int") || rightType.equals("float"))){
                throw new RuntimeException("Comparison operators require numeric operands");
            }
            return "boolean";
        } else if (operator.matches("\\+|\\-|\\*|\\/")) {
            if (!leftType.equals("int") && !leftType.equals("float") || !rightType.equals("int") && !rightType.equals("float")) {
                throw new RuntimeException("Arithmetic operators require numeric operands");
            }
            return (leftType.equals("float") || rightType.equals("float")) ? "float" : "int"; // Preserve type promotion
        }
        throw new RuntimeException("Unsupported binary operator: " + operator);
    }

}