package transpiler;

import java.util.List;
import parser.Stmt;

public class Analyzer {
    private SemanticAnalyzer semanticAnalyzer;
    
    public Analyzer() {
        this.semanticAnalyzer = new SemanticAnalyzer();
    }
    
    public void analyze(List<Stmt> statements) {
        for (Stmt stmt : statements) {
            semanticAnalyzer.analyze(stmt);
        }
    }
}
