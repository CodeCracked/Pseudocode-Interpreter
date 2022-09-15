package interpreter.impl.tokens;

public enum TokenType
{
    STATEMENT_KEYWORD,
    TYPE_KEYWORD,
    
    IDENTIFIER,
    
    STRING_LITERAL,
    INTEGER_LITERAL,
    REAL_LITERAL,
    
    ASSIGN,
    PLUS,
    MINUS,
    MUL,
    DIV,
    MOD,
    
    LPAREN,
    RPAREN,
    COMMA,
    
    NEWLINE,
    EOF
}
