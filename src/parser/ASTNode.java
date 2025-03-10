//Abstract Syntax Tree

abstract class ASTNode{}

class LiteralNode extends ASTNode{
    String value;

    LiteralNode (String value){
        this.value = value;
    }
}

class BinaryOpNode extends ASTNode{
    ASTNode left;
    String operator;
    ASTNode right;

    BinaryOpNode(String operator, ASTNode right, ASTNode left){
        this.left = left;
        this.right = right;
        this.operator = operator;
    }
}

class AssignmentNode extends ASTNode{
    String type;
    String name;
    ASTNode value;

    AssignmentNode(String type, String name, ASTNode value){

        this.name = name;
        this.type = type;
        this.value = value;
    }
}

class ControlStructs extends ASTNode{

}

