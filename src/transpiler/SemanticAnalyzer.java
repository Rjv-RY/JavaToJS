package transpiler;

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
    }

    private boolean isValidConditionType(String type) {
        // Define which types can be implicitly converted to boolean
        switch (type) {
            case "boolean":
                return true; // Already a boolean, no conversion needed
            case "int":
            case "float":
                return true; // Numeric types (0 = false, anything else = true)
            case "string":
                return true; // String types (empty = false, non-empty = true)
            default:
                return false; // Other types can't be converted
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

    private void analyzeVarStmt(VarStmt stmt){
        String varName = stmt.getName().getValue();
        String varType = stmt.getType().getValue();

        symbolTable.declareVariable(varName, varType);

        if(stmt.getInitialzer() != null){
            String inferredType = analyzeExpression(stmt.getInitialzer()); // Get the inferred type
            if (!inferredType.equals(varType)) {
                throw new RuntimeException("Type mismatch: expected " + varType + " but got " + inferredType);
            }
        }
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
        }
        throw new RuntimeException("Unsupported expression type");
    }

    private String inferLiteralType(LiteralExpr expr){
        String value = String.valueOf(expr.getValue());
        if (value.matches("\\d+")) return "int";
        if (value.matches("\\d+\\.\\d+")) return "float";
        if (value.equals("true") || value.equals("false")) return "boolean";
        return "string";
    }

    private void analyzeIfStmt(IfStmt stmt){
        String conditionType = analyzeExpression(stmt.getCondition());
        if (!isValidConditionType(conditionType)) {
            throw new RuntimeException("If statement condition must be coercible to boolean, got: " + conditionType);
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
        symbolTable.enterScope();
        analyze(stmt.getBody());
        symbolTable.exitScope();
    }
}