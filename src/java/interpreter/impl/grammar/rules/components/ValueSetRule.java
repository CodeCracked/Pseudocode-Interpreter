package interpreter.impl.grammar.rules.components;

import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.components.ValueSetNode;
import interpreter.impl.grammar.rules.GrammarRules;
import interpreter.impl.tokens.TokenType;

import java.util.ArrayList;
import java.util.List;

public class ValueSetRule implements IGrammarRule
{
    @Override
    public Result<AbstractNode> build(Parser parser)
    {
        Result<AbstractNode> result = new Result<>();
        List<AbstractValuedNode> values = new ArrayList<>();
        
        // First Expression
        AbstractNode expression = result.register(GrammarRules.EXPRESSION.build(parser));
        if (result.error() != null) return result;
        else values.add((AbstractValuedNode) expression);
        
        // Optional Additional Expressions
        while (parser.getCurrentToken().type() == TokenType.COMMA)
        {
            result.registerAdvancement();
            parser.advance();
    
            expression = result.register(GrammarRules.EXPRESSION.build(parser));
            if (result.error() != null) return result;
            else values.add((AbstractValuedNode) expression);
        }
        
        return result.success(new ValueSetNode(values));
    }
}
