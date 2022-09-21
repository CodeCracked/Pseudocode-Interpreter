package interpreter.impl.grammar.rules.expressions;

import interpreter.core.lexer.Token;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.expressions.UnaryOpNode;
import interpreter.impl.grammar.rules.GrammarRules;
import interpreter.impl.tokens.TokenType;

import java.util.Set;

public class ComparisonExpressionRule implements IGrammarRule
{
    private final Set<Enum<?>> comparisonOperators = Set.of(TokenType.EQUALS, TokenType.NOT_EQUALS, TokenType.GREATER, TokenType.LESS, TokenType.GREATER_EQUAL, TokenType.LESS_EQUAL);
    
    @Override
    public Result<AbstractNode> build(Parser parser)
    {
        Result<AbstractNode> result = new Result<>();
        
        // Optional NOT Operator
        if (parser.getCurrentToken().type() == TokenType.NOT)
        {
            // Grab NOT Operator
            Token notOperator = parser.getCurrentToken();
            result.registerAdvancement();
            parser.advance();
    
            // Argument
            AbstractValuedNode argument = (AbstractValuedNode) result.register(build(parser));
            if (result.error() != null) return result;
            else return result.success(new UnaryOpNode(notOperator, argument));
        }
        else return GrammarRules.binaryOperationRule(parser, GrammarRules.ARITHMETIC_EXPRESSION, comparisonOperators);
    }
}
