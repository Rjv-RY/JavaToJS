package tests;

import java.util.List;
import lexer.*;
import parser.*;
import transpiler.*;

public class AnalyzerTests {
    public static void main(String[] args) {
        testValidExpression();
        testInvalidBinaryOperands();
    }

    private static void testValidExpression(){
        String code =  "int[] nums = new int[true];";
        analyzeAndExpectSuccess(code);
    }

    private static void testInvalidBinaryOperands() {
        String code = "int x = 3.14;";
        analyzeAndExpectFailure(code);
    }

    private static void analyzeAndExpectSuccess(String code){
        try {
            runAnalyzer(code);
            System.out.println("✔ Test passed");
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

    private static void runAnalyzer(String code){
        Lexer lexer = new Lexer(code);
        lexer.tokenize();
        List<Token> tokens = lexer.getTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        Analyzer analyzer = new Analyzer();
        analyzer.analyze(statements);
    }
}