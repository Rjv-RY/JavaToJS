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

    public void analyze(Stmt stmt) {
        if (stmt instanceof VarStmt varStmt){
            analyzeVarStmt(varStmt);
        } else if (stmt instanceof  IfStmt ifStmt){
            analyzeIfStmt(ifStmt);
        } else if (stmt instanceof WhileStmt whileStmt){
            analyzeWhileStmt(whileStmt);
        } else if (stmt instanceof  BlockStmt blockStmt){
            analyzeBlockStmt(blockStmt);
        }
        
    }

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

    private String analyzeAssignmentExpr(AssignmentExpr expr){
        String varName = expr.getName().getValue();
        if (!symbolTable.isDeclared(varName)){
            throw new RuntimeException("Undeclared variable: " + varName);
        }
    
        String expectedType = symbolTable.getVariableType(varName);
        String inferredType = analyzeExpression(expr.getRight()); // Get inferred type
    
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

    //needs testing, THROUGHLY
    private void analyzeVarStmt(VarStmt stmt){
        String varName = stmt.getName().getValue();
        String varType = stmt.getType().getValue();
        boolean isArray = stmt.isArray();

        symbolTable.declareVariable(varName, varType);

        if (stmt.getInitialzer() != null){
            Expr initializer = stmt.getInitialzer();
            String inferredType = analyzeExpression(initializer);

            if (initializer instanceof NewArrayExpr newArrayExpr){
                String arrayBaseType = newArrayExpr.getType().getValue();
                if (!arrayBaseType.equals(varType)){
                    throw new RuntimeException("Array type mismatch: variable declared as " + varType + "[] but initialized with " + arrayBaseType + "[]");
                }
                int declaredDimensions = isArray ? 1 : 0;
                int initializerDimensions = newArrayExpr.getDimensions().size();
            
                if (declaredDimensions > 0 && declaredDimensions != initializerDimensions) {
                    throw new RuntimeException("Array dimension mismatch: variable declared with " + declaredDimensions + " dimensions but initialized with " + initializerDimensions + " dimensions");
                }
            } else if (initializer instanceof ArrayLiteralExpr) {
                if (!isArray){
                    throw new RuntimeException("Cannot initialize non-array variable with array literal");
                }
                if(!inferredType.endsWith("[]")){
                    throw new RuntimeException("Expected array type but got " + inferredType);
                }

                String inferredBaseType = inferredType.substring(0, inferredType.length() - 2);
                if(!inferredBaseType.equals(varType)){
                    throw new RuntimeException("Array type mismatch: expected " + varType + "[] but got " + inferredBaseType + "[]");
                }
            } else if (isArray){
                throw new RuntimeException("Array variable must be initialized with array expression");
            } else if (!inferredType.equals(varType)) {
                throw new RuntimeException("Type mismatch: expected " + varType + " but got " + inferredType);
            }
        }

        // if(stmt.getInitialzer() != null){
        //     String inferredType = analyzeExpression(stmt.getInitialzer()); // Get the inferred type
        //     if (!inferredType.equals(varType)) {
        //         throw new RuntimeException("Type mismatch: expected " + varType + " but got " + inferredType);
        //     }
        // }
    }

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
        }
        throw new RuntimeException("Unsupported expression type");
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
            default:
                throw new RuntimeException("Unsupported unary operator: " + operator);
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

        String arrayType = baseType;
        for (int i  = 0; i < expr.getDimensions().size(); i++){
            arrayType += "[]";
        }

        return arrayType;
    }
}