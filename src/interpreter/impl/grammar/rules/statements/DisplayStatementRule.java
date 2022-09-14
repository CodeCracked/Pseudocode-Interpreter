package interpreter.impl.grammar.rules.statements;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.ParseResult;
import interpreter.core.parser.Parser;
import interpreter.impl.grammar.nodes.statements.DisplayStatementNode;
import interpreter.impl.tokens.TokenType;

public class DisplayStatementRule implements IGrammarRule
{
    @Override
    public ParseResult build(Parser parser)
    {
        ParseResult result = new ParseResult();
    
        Token keyword = parser.getCurrentToken();
        if (!keyword.isKeyword(TokenType.STATEMENT_KEYWORD, "Display", 1)) return result.failure(new SyntaxException(parser, "Expected keyword 'Display'! Did you forget proper capitalization?"));
        result.registerAdvancement();
        parser.advance();
    
        Token value = parser.getCurrentToken();
        if (value.type() != TokenType.STRING_LITERAL) return result.failure(new SyntaxException(parser, "Expected string literal, found " + value.type() + "!"));
        result.registerAdvancement();
        parser.advance();
        
        Token newline = parser.getCurrentToken();
        if (newline.type() != TokenType.NEWLINE) return result.failure(new SyntaxException(parser, "Expected newline!"));
        result.registerAdvancement();
        parser.advance();
        
        return result.success(new DisplayStatementNode(keyword, value));
    }
}
