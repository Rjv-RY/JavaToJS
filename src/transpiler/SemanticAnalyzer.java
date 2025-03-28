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
        if (stmt instanceof VarStmt){
            analyzeVarStmt((VarStmt) stmt);
        }
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
        if (expr instanceof LiteralExpr) {
            String actualType = inferLiteralType((LiteralExpr) expr);
            if (!actualType.equals(expectedType)) {
                throw new RuntimeException("Type mismatch: expected " + expectedType + " but got " + actualType);
            }
        }
    }

    private String inferLiteralType(LiteralExpr expr){
        String value = String.valueOf(expr.getValue());
        if (value.matches("\\d+")) return "int";
        if (value.matches("\\d+\\.\\d+")) return "float";
        if (value.equals("true") || value.equals("false")) return "boolean";
        return "string"; // Default case
    }
}