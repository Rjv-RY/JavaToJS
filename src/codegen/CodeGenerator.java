package codegen;

import java.util.List;
import lexer.TokenType;
import parser.*;
import parser.exprs.*;
import parser.stmts.*;

public class CodeGenerator {
    private StringBuilder builder = new StringBuilder();

    public String generate(List<Stmt> statements) {
        for (Stmt stmt : statements) {
            emitStmt(stmt);
        }
        return builder.toString();
    }

    private void emitExpr(Expr expr) {
        if (expr instanceof LiteralExpr lit) {
            Object value = lit.getValue();
            TokenType type = lit.getTokenType();

            switch (type) {
                case STRING_LITERALS:
                    builder.append("\"").append(value).append("\"");
                    break;
                case FLOAT:
                    builder.append(value).append("f"); // optional, depending on whether you want to retain 'f'
                    break;
                case BOOLEAN:
                    builder.append(value);
                    break;
                case NUMBER_LITERALS:
                    builder.append(value);
                    break;
                default:
                    builder.append("/* Unsupported literal */").append(type).append(" */");
            }

        } else if (expr instanceof VariableExpr var) {
            builder.append(var.getVar());

        } else if (expr instanceof BinaryExpr bin) {
            builder.append("(");
            emitExpr(bin.getLeft());
            builder.append(" ").append(bin.getOperator().getValue()).append(" ");
            emitExpr(bin.getRight());
            builder.append(")");
        }
    }

    private void emitStmt(Stmt stmt){
        if (stmt instanceof VarStmt varStmt) {
            builder.append("let ")
                   .append(varStmt.getName().getValue())
                   .append(" = ");
            emitExpr(varStmt.getInitialzer());
            builder.append(";\n");
        } else if (stmt instanceof ExprStmt exprStmt) {
            emitExpr(exprStmt.getExpr());
            builder.append(";\n");
        }
    }
}