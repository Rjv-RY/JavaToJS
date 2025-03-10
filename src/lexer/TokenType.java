package lexer;

/* 
for reference

Java keywords: 

control flow- if, else, return, for, while, switch, 
case, default, break, continue

class & method structure- class, interface, extends, 
implements, package, import

access modifiers- public, private, protected, static, 
final, abstract

primitive types- int, double, boolean, char, void

exception handling- try, catch, throw, throws, finally

boolenas- true, false, null


Operators:

logical:: &&, ||, !

arithmetic:: +, -, *, /, %

comparison:: ==, !=, >, <, >=, <=

assignment:: =, +=, -=, *=, /=

(incre/decre)ment:: ++, --

*/

public enum TokenType{
    //keywords
    IF, ELSE, RETURN, FOR, WHILE, BREAK, DEFAULT, CONTINUE, SWITCH, CASE, 
    CLASS, INTERFACE, EXTENDS, IMPLEMENTS, PACKAGE, IMPORT, 
    PUBLIC, PRIVATE, PROTECTED, STATIC, FINAL, ABSTRACT, 
    VOID, INT, DOUBLE, CHAR, BOOLEAN,
    TRY, CATCH, THROW, THROWS, FINALLY,
    TRUE, FALSE, NULL, FLOAT_LITERALS,
    //operators
    PLUS, MINUS, DIVIDE, MULTIPLY, MOD, EQUALS, NOT_EQUALS, 
    GREATER_THAN, LESS_THAN, GREATER_THAN_EQUALS, LESS_THAN_EQUALS, AND, OR, NOT,
    BITWISE_AND, BITWISE_OR, 
    //assignment
    ASSIGN, PLUS_EQUALS, MINUS_EQUALS, MULT_EQUALS, DIV_EQUALS, 
    INCREMENT, DECREMENT,
    //identifier
    IDENTIFIER,
    //literals
    STRING_LITERALS, NUMBER_LITERALS, CHAR_LITERALS, BOOLEAN_LITERALS,
    //puntcuation
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, LEFT_BRACKET, RIGHT_BRACKET, SEMICOLON, COMMA, DOT,
    //comments
    LINE_COMMENT, BLOCK_COMMENT,
    //special
    WHITESPACE, UNKNOWN, EOF,
    FLOAT,
    VAR, PRINT, PRINTLN
}

