package interpreter.impl.grammar.rules;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.flow.BlockNode;
import interpreter.impl.grammar.nodes.expressions.BinaryOpNode;
import interpreter.impl.grammar.rules.blocks.ModuleDefinitionRule;
import interpreter.impl.grammar.rules.components.ArgumentListRule;
import interpreter.impl.grammar.rules.components.ParameterListRule;
import interpreter.impl.grammar.rules.components.ValueSetRule;
import interpreter.impl.grammar.rules.expressions.*;
import interpreter.impl.grammar.rules.statements.*;
import interpreter.impl.tokens.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GrammarRules
{
    public static IGrammarRule PROGRAM = new ProgramRule();
    public static IGrammarRule MODULE_DEFINITION = new ModuleDefinitionRule();
    
    public static IGrammarRule STATEMENT = new StatementRule();
    public static IGrammarRule CONSTANT_STATEMENT = new ConstantStatementRule();
    public static IGrammarRule DECLARE_STATEMENT = new DeclareStatementRule();
    public static IGrammarRule DISPLAY_STATEMENT = new DisplayStatementRule();
    public static IGrammarRule SET_STATEMENT = new SetStatementRule();
    public static IGrammarRule INPUT_STATEMENT = new InputStatementRule();
    public static IGrammarRule CALL_STATEMENT = new CallStatementRule();
    public static IGrammarRule IF_STATEMENT = new IfStatementRule();
    public static IGrammarRule SWITCH_STATEMENT = new SwitchStatementRule();
    public static IGrammarRule WHILE_STATEMENT = new WhileStatementRule();
    public static IGrammarRule DO_STATEMENT = new DoStatementRule();
    public static IGrammarRule FOR_STATEMENT = new ForStatementRule();
    
    public static IGrammarRule VALUE_SET = new ValueSetRule();
    public static IGrammarRule ARGUMENT_LIST = new ArgumentListRule();
    public static IGrammarRule PARAMETER_LIST = new ParameterListRule();
    
    public static IGrammarRule EXPRESSION = new ExpressionRule();
    public static IGrammarRule COMPARISON_EXPRESSION = new ComparisonExpressionRule();
    public static IGrammarRule ARITHMETIC_EXPRESSION = new ArithmeticExpressionRule();
    public static IGrammarRule FACTOR = new FactorRule();
    public static IGrammarRule EXPONENT = new ExponentRule();
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
    
    public static Result<AbstractNode> block(Parser parser)
    {
        return block(parser, parser.getCurrentIndent());
    }
    public static Result<AbstractNode> block(Parser parser, int indentation)
    {
        Result<AbstractNode> result = new Result<>();
        List<AbstractNode> statements = new ArrayList<>();
        
        while (parser.getCurrentToken().matches(TokenType.INDENT, indentation))
        {
            result.registerAdvancement();
            parser.advance();
            
            Result<AbstractNode> statementResult = result.registerIssues(GrammarRules.STATEMENT.build(parser));
            if (result.error() != null) return result;
            statements.add(statementResult.get());
        }
    
        if (statements.size() == 0) return result.failure(new SyntaxException(parser, "Expected indentation of size " + indentation + ", then a statement!"));
        else return result.success(new BlockNode(statements));
    }
    
    public static Result<Token> endStatement(Parser parser, int indentation, String statementKeyword)
    {
        return endStatement(parser, indentation, TokenType.STATEMENT_KEYWORD, statementKeyword);
    }
    public static Result<Token> endStatement(Parser parser, int indentation, Enum<?> keywordType, String keyword)
    {
        Result<Token> result = new Result<>();
        
        if (indentation != 0)
        {
            // Indent
            if (!parser.getCurrentToken().matches(TokenType.INDENT, indentation)) return result.failure(new SyntaxException(parser, "Expected indentation of size " + indentation + "!"));
            result.registerAdvancement();
            parser.advance();
        }
    
        // End
        if (parser.getCurrentToken().type() != TokenType.END) return result.failure(new SyntaxException(parser, "Expected 'End " + keyword + "'!"));
        result.registerAdvancement();
        parser.advance();
    
        // Statement Keyword
        Token closeToken = parser.getCurrentToken();
        if (!closeToken.matches(keywordType, keyword)) return result.failure(new SyntaxException(parser, "Expected '" + keyword + "'!"));
        result.registerAdvancement();
        parser.advance();
    
        // Newline
        if (parser.getCurrentToken().type() != TokenType.NEWLINE) return result.failure(new SyntaxException(parser, "Expected newline!"));
        result.registerAdvancement();
        parser.advance();
        
        return result.success(closeToken);
    }
}
