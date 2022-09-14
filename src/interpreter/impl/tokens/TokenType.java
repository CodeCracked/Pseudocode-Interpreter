package interpreter.impl.tokens;

public enum TokenType
{
    STATEMENT_KEYWORD,
    TYPE_KEYWORD,
    OPERATOR,
    
    IDENTIFIER,
    
    STRING_LITERAL,
    INTEGER_LITERAL,
    REAL_LITERAL,
    
    ASSIGN,
    
    NEWLINE,
    EOF
}
