package interpreter.impl.grammar.rules.components;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.components.ArgumentListNode;
import interpreter.impl.grammar.nodes.components.ValueSetNode;
import interpreter.impl.grammar.rules.GrammarRules;
import interpreter.impl.tokens.TokenType;

public class ArgumentListRule implements IGrammarRule
{
    @Override
    public Result<AbstractNode> build(Parser parser)
    {
        Result<AbstractNode> result = new Result<>();
        
        // Open Token
        Token openToken = parser.getCurrentToken();
        if (openToken.type() != TokenType.LPAREN) return result.failure(new SyntaxException(parser, "Expected '('!"));
        result.registerAdvancement();
        parser.advance();
        
        // Check Empty List
        if (parser.getCurrentToken().type() == TokenType.RPAREN)
        {
            Token closeToken = parser.getCurrentToken();
            result.registerAdvancement();
            parser.advance();
            return result.success(new ArgumentListNode(openToken, null, closeToken));
        }
        
        // Read Arguments
        ValueSetNode argumentValues = (ValueSetNode) result.register(GrammarRules.VALUE_SET.build(parser));
        if (result.error() != null) return result;
        
        // Close Token
        Token closeToken = parser.getCurrentToken();
        if (closeToken.type() != TokenType.RPAREN) return result.failure(new SyntaxException(parser, "Expected ')'!"));
        result.registerAdvancement();
        parser.advance();
        
        return result.success(new ArgumentListNode(openToken, argumentValues, closeToken));
    }
}
