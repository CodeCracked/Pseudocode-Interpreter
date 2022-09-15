package interpreter.impl.grammar.rules.expressions;

import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.rules.GrammarRules;
import interpreter.impl.tokens.TokenType;

import java.util.Set;

public class ExpressionRule implements IGrammarRule
{
    private final Set<Enum<?>> arithmeticOperators = Set.of(TokenType.PLUS, TokenType.MINUS);
    
    @Override
    public Result<AbstractNode> build(Parser parser)
    {
        return GrammarRules.binaryOperationRule(parser, GrammarRules.FACTOR, arithmeticOperators);
    }
}
