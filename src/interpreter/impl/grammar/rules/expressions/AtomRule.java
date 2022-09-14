package interpreter.impl.grammar.rules.expressions;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.ParseResult;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.impl.grammar.nodes.expressions.LiteralValueNode;
import interpreter.impl.tokens.TokenType;

public class AtomRule implements IGrammarRule
{
    @Override
    public ParseResult build(Parser parser)
    {
        ParseResult result = new ParseResult();
        AbstractNode node = null;
        
        // Literals
        ParseResult literalResult = literal(parser);
        if (literalResult.error() == null) node = result.register(literalResult);
        
        return result.success(node);
    }
    
    private ParseResult literal(Parser parser)
    {
        ParseResult result = new ParseResult();
        
        Token literal = parser.getCurrentToken();
        if (literal.type() == TokenType.STRING_LITERAL || literal.type() == TokenType.INTEGER_LITERAL || literal.type() == TokenType.REAL_LITERAL)
        {
            result.registerAdvancement();
            parser.advance();
            return result.success(new LiteralValueNode(literal));
        }
        else return result.failure(new SyntaxException(parser, "Expected literal value, found " + literal.type().name() + "!"));
    }
}
