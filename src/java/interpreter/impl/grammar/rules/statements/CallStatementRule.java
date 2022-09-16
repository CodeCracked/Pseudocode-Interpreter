package interpreter.impl.grammar.rules.statements;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.components.ArgumentListNode;
import interpreter.impl.grammar.nodes.statements.CallStatementNode;
import interpreter.impl.grammar.rules.GrammarRules;
import interpreter.impl.tokens.TokenType;

public class CallStatementRule implements IGrammarRule
{
    @Override
    public Result<AbstractNode> build(Parser parser)
    {
        Result<AbstractNode> result = new Result<>();
        
        // Keyword
        Token keyword = parser.getCurrentToken();
        if (!keyword.isKeyword(TokenType.STATEMENT_KEYWORD, "Call")) return result.failure(new SyntaxException(parser, "Expected keyword 'Call'! Did you forget proper capitalization?"));
        result.registerAdvancement();
        parser.advance();
        
        // Identifier
        Token identifier = parser.getCurrentToken();
        if (identifier.type() != TokenType.IDENTIFIER) return result.failure(new SyntaxException(parser, "Expected identifier!"));
        result.registerAdvancement();
        parser.advance();
        
        // Arguments
        ArgumentListNode arguments = (ArgumentListNode) result.register(GrammarRules.ARGUMENT_LIST.build(parser));
        if (result.error() != null) return result;
        
        // Newline
        if (parser.getCurrentToken().type() != TokenType.NEWLINE) return result.failure(new SyntaxException(parser, "Expected newline!"));
        result.registerAdvancement();
        parser.advance();
        
        return result.success(new CallStatementNode(keyword, identifier, arguments));
    }
}
