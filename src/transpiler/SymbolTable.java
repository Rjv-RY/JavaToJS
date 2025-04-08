package transpiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SymbolTable {
    private Map<String, Map<String, String>> scopes;

    private Stack<String> scopeStack;
    private int scopeCounter;

    public SymbolTable() {
        this.scopes = new HashMap<>();
        this.scopeStack = new Stack<>();
        this.scopeCounter = 0;
        
        // Create global scope
        enterScope();
    }

    public void enterScope() {
        String scopeName = "scope" + scopeCounter++;
        scopes.put(scopeName, new HashMap<>());
        scopeStack.push(scopeName);
    }

    public void exitScope() {
        if (scopeStack.size() > 1) {
            scopeStack.pop();
        }
    }

    public void declareVariable(String name, String type){
        String currentScope = scopeStack.peek();
        Map<String, String> currentScopeVars = scopes.get(currentScope);
        
        if (currentScopeVars.containsKey(name)) {
            throw new RuntimeException("Variable '" + name + "' is already declared in this scope.");
        }
        
        currentScopeVars.put(name, type);
    }

    public String getVariableType(String name){
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            String scope = scopeStack.get(i);
            Map<String, String> scopeVars = scopes.get(scope);
            
            if (scopeVars.containsKey(name)) {
                return scopeVars.get(name);
            }
        }
        
        throw new RuntimeException("Variable '" + name + "' is not declared.");
    }

    public boolean isDeclared(String name) {
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            String scope = scopeStack.get(i);
            Map<String, String> scopeVars = scopes.get(scope);
            
            if (scopeVars.containsKey(name)) {
                return true;
            }
        }
        
        return false;
    }
}