package interpreter.impl.grammar.rules.statements;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.statements.InputStatementNode;
import interpreter.impl.tokens.TokenType;

public class InputStatementRule implements IGrammarRule
{
    @Override
    public Result<AbstractNode> build(Parser parser)
    {
        Result<AbstractNode> result = new Result<>();
        
        // Keyword
        Token keyword = parser.getCurrentToken();
        if (!keyword.isKeyword(TokenType.STATEMENT_KEYWORD, "Input", 1)) return result.failure(new SyntaxException(parser, "Expected keyword 'Input'! Did you forget proper capitalization?"));
        result.registerAdvancement();
        parser.advance();
        
        // Identifier
        Token identifier = parser.getCurrentToken();
        if (identifier.type() != TokenType.IDENTIFIER) return result.failure(new SyntaxException(parser, "Expected identifier!"));
        result.registerAdvancement();
        parser.advance();
        
        // Newline
        if (parser.getCurrentToken().type() != TokenType.NEWLINE) return result.failure(new SyntaxException(parser, "Expected newline!"));
        result.registerAdvancement();
        parser.advance();
        
        return result.success(new InputStatementNode(keyword, identifier));
    }
}
