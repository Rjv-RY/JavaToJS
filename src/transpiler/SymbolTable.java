package transpiler;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, String> variables;

    public SymbolTable(){
        this.variables = new HashMap<>();
    }

    public void declareVariable(String name, String type){
        if (variables.containsKey(name)){
            throw new RuntimeException("Variable '" + name + "' is already declared.");
        }
        variables.put(name, type);
    }

    public String getVariableType(String name){
        if(!variables.containsKey(name)){
            throw new RuntimeException("Variable '" + name + "' is not declared.");
        }
        return variables.get(name);
    }

    public boolean isDeclared(String name) {
        return variables.containsKey(name);
    }
}
