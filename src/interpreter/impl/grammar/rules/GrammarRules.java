package interpreter.impl.grammar.rules;

import interpreter.core.lexer.Token;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.expressions.BinaryOpNode;
import interpreter.impl.grammar.rules.blocks.BlockRule;
import interpreter.impl.grammar.rules.components.ParameterListRule;
import interpreter.impl.grammar.rules.components.ValueSetRule;
import interpreter.impl.grammar.rules.expressions.AtomRule;
import interpreter.impl.grammar.rules.expressions.ExpressionRule;
import interpreter.impl.grammar.rules.expressions.FactorRule;
import interpreter.impl.grammar.rules.statements.*;

import java.util.Set;

public class GrammarRules
{
    public static IGrammarRule PROGRAM = new ProgramRule();
    
    public static IGrammarRule BLOCK = new BlockRule();
    
    public static IGrammarRule STATEMENT = new StatementRule();
    public static IGrammarRule DECLARE_STATEMENT = new DeclareStatementRule();
    public static IGrammarRule DISPLAY_STATEMENT = new DisplayStatementRule();
    public static IGrammarRule SET_STATEMENT = new SetStatementRule();
    public static IGrammarRule INPUT_STATEMENT = new InputStatementRule();
    
    public static IGrammarRule VALUE_SET = new ValueSetRule();
    public static IGrammarRule PARAMETER_LIST = new ParameterListRule();
    
    public static IGrammarRule EXPRESSION = new ExpressionRule();
    public static IGrammarRule FACTOR = new FactorRule();
    public static IGrammarRule ATOM = new AtomRule();
    
    public static Result<AbstractNode> binaryOperationRule(Parser parser, IGrammarRule argumentRule, Set<Enum<?>> operations)
    {
        Result<AbstractNode> result = new Result<>();
        AbstractValuedNode left = (AbstractValuedNode) result.register(argumentRule.build(parser));
        if (result.error() != null) return result;
        
        while (parser.getCurrentToken() != null)
        {
            boolean foundOperation = false;
            for (Enum<?> operationCheck : operations)
            {
                if (parser.getCurrentToken().type() == operationCheck)
                {
                    Token operation = parser.getCurrentToken();
                    result.registerAdvancement();
                    parser.advance();
                    
                    AbstractValuedNode right = (AbstractValuedNode) result.register(argumentRule.build(parser));
                    if (result.error() != null) return result;
                    
                    left = new BinaryOpNode(left, operation, right);
                    foundOperation = true;
                    break;
                }
            }
            if (!foundOperation) break;
        }
        
        return result.success(left);
    }
}
