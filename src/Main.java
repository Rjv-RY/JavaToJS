import codegen.*;
import java.util.List;
import lexer.*;
import parser.*;
import transpiler.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Running Java to JavaScript Transpiler Tests");
        System.out.println("=========================================");
        runTests();
    }
    
    public static void testValidExpression(){
        //Pass value here as String
        String code = "int x = 7;";
        analyzeAndExpectSuccess(code);
    }
    
    private static void runTests() {
        testValidExpression();
        
        // testInvalidBinaryOperands();
    }
    
    public static void testInvalidBinaryOperands() {
        String code = "int x = 3.14;";
        analyzeAndExpectFailure(code);
    }
    
    private static void analyzeAndExpectSuccess(String code){
        try {
            List<Stmt> statements = runAnalyzer(code);
            CodeGenerator generator = new CodeGenerator();
            String jsCode = generator.generate(statements);
            System.out.println("✔ Test passed");
            System.out.println("Generated JS:\n" + jsCode);
        } catch (Exception e) {
            System.out.println("❌ Test failed: Unexpected error: " + e.getMessage());
        }
    }
    
    private static void analyzeAndExpectFailure(String code){
        try {
            runAnalyzer(code);
            System.out.println("❌ Test failed: Expected error, but none occurred.");
        } catch (RuntimeException e) {
            System.out.println("✔ Test passed (caught error): " + e.getMessage());
        }  
    }
    
    private static List<Stmt> runAnalyzer(String code){
        Lexer lexer = new Lexer(code);
        lexer.tokenize();
        List<Token> tokens = lexer.getTokens();
        System.out.println(tokens);
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        Analyzer analyzer = new Analyzer();
        analyzer.analyze(statements);
        return statements;
    }
}