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

    private String generateMultiDimArray(List<Expr> dims, int index) {
        StringBuilder arr = new StringBuilder();
        arr.append("Array(");
        emitExpr(dims.get(index));
        arr.append(").fill(");
        if (index + 1 < dims.size()) {
            arr.append(generateMultiDimArray(dims, index + 1));
        } else {
            arr.append("null");
        }
        arr.append(")");
        return arr.toString();
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
                    builder.append(value).append("f");
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
        } else if (expr instanceof UnaryExpr unary) {
            builder.append("(");
            builder.append(unary.getOperator().getValue());
            emitExpr(unary.getRight());
            builder.append(")");
        } else if (expr instanceof AssignmentExpr assign) {
            builder.append(assign.getName().getValue())
                   .append(" ")
                   .append(assign.getAssign().getValue()) // should usually be "="
                   .append(" ");
            emitExpr(assign.getRight());
        } else if (expr instanceof ArrayLiteralExpr arrayLit) {
            builder.append("[");
            List<Expr> elements = arrayLit.getElements();
            for (int i = 0; i < elements.size(); i++) {
                emitExpr(elements.get(i));
                if (i < elements.size() - 1) builder.append(", ");
            }
            builder.append("]");
        } else if (expr instanceof PostfixExpr post) {
            emitExpr(post.getOperand());
            builder.append(post.getOperator().getValue()); // "++" or "--"
        } else if (expr instanceof GroupingExpr group) {
            builder.append("(");
            emitExpr(group.getExpr());
            builder.append(")");
        } else if (expr instanceof NewArrayExpr newArr) {
            List<Expr> dims = newArr.getDimensions();
            if (dims.size() == 1) {
                //1D arrays
                builder.append("Array(");
                emitExpr(dims.get(0));
                builder.append(").fill(null)");
            } else {
                //Mult-D arrays
                builder.append(generateMultiDimArray(dims, 0));
            }
        }
    }

    private void emitStmt(Stmt stmt){
        if (stmt instanceof VarStmt varStmt) {
            builder.append("let ").append(varStmt.getName().getValue());
            Expr initializer = varStmt.getInitialzer();
            if (initializer != null) {
                builder.append(" = ");
                emitExpr(initializer);
            }
            builder.append(";\n");
        }else if (stmt instanceof ExprStmt exprStmt) {
            emitExpr(exprStmt.getExpr());
            builder.append(";\n");
        } else if (stmt instanceof BlockStmt block) {
            builder.append("{\n");
            for (Stmt s : block.getStatements()) {
                emitStmt(s);
            }
            builder.append("}\n");
        } else if (stmt instanceof IfStmt ifStmt) {
            builder.append("if (");
            emitExpr(ifStmt.getCondition());
            builder.append(") ");
            emitStmt(ifStmt.getThenBranch());
        
            if (ifStmt.getElseBranch() != null) {
                builder.append(" else ");
                emitStmt(ifStmt.getElseBranch());
            }
        } else if (stmt instanceof PrintStmt printStmt) {
            builder.append("console.log(");
            emitExpr(printStmt.getExpr());
            builder.append(");\n");
        } else if (stmt instanceof WhileStmt whileStmt) {
            builder.append("while (");
            emitExpr(whileStmt.getCondition());
            builder.append(") ");
            emitStmt(whileStmt.getBody());
        }
    }
}