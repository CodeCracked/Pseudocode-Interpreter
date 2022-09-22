package interpreter.impl.tokens;

public enum TokenType
{
    STATEMENT_KEYWORD,
    TYPE_KEYWORD,
    THEN,
    ELSE,
    
    IDENTIFIER,
    
    STRING_LITERAL,
    INTEGER_LITERAL,
    REAL_LITERAL,
    
    ASSIGN,
    
    PLUS,
    MINUS,
    MUL,
    DIV,
    POW,
    MOD,
    
    EQUALS,
    NOT_EQUALS,
    GREATER,
    LESS,
    GREATER_EQUAL,
    LESS_EQUAL,
    
    NOT,
    AND,
    OR,
    
    LPAREN,
    RPAREN,
    COMMA,
    
    MODULE,
    END,
    REF,
    
    INDENT,
    NEWLINE,
    EOF
}
