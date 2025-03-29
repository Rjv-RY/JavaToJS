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
        }
    }

    private void analyzeAssignmentExpr(AssignmentExpr expr){
        String varName = expr.getName().getValue();

        if(!symbolTable.isDeclared(varName)){
            throw new RuntimeException("Undeclared variable: " + varName);
        }

        String expectedType = symbolTable.getVariableType(varName);
        analyzeExpression(expr.getRight(), expectedType);
    }

    private void analyzeVarStmt(VarStmt stmt){
        String varName = stmt.getName().getValue();
        String varType = stmt.getType().getValue();

        symbolTable.declareVariable(varName, varType);

        if(stmt.getInitialzer() != null){
            analyzeExpression(stmt.getInitialzer(), varType);
        }
    }

    private void analyzeExpression(Expr expr, String expectedType) {
        if (expr instanceof VariableExpr variableExpr) {
            String varName = variableExpr.getVar();
            if (!symbolTable.isDeclared(varName)) {
                throw new RuntimeException("Undeclared variable: " + varName);
            }
        }else if (expr instanceof LiteralExpr literalExpr) {
            String actualType = inferLiteralType(literalExpr);
            if (!actualType.equals(expectedType)) {
                throw new RuntimeException("Type mismatch: expected " + expectedType + " but got " + actualType);
            }
        } else if (expr instanceof AssignmentExpr assignmentExpr) {
            analyzeAssignmentExpr(assignmentExpr);
        }
    }

    private String inferLiteralType(LiteralExpr expr){
        String value = String.valueOf(expr.getValue());
        if (value.matches("\\d+")) return "int";
        if (value.matches("\\d+\\.\\d+")) return "float";
        if (value.equals("true") || value.equals("false")) return "boolean";
        return "string";
    }
}