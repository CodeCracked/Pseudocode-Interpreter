package interpreter.impl.grammar.rules.statements;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.statements.ForStatementNode;
import interpreter.impl.grammar.rules.GrammarRules;
import interpreter.impl.tokens.TokenType;

public class ForStatementRule implements IGrammarRule
{
    @Override
    public Result<AbstractNode> build(Parser parser)
    {
        Result<AbstractNode> result = new Result<>();
        int indentation = parser.getCurrentIndent();
    
        // For Keyword
        Token forKeyword = parser.getCurrentToken();
        if (!forKeyword.isKeyword(TokenType.STATEMENT_KEYWORD, "For")) return result.failure(new SyntaxException(parser, "Expected 'For'!"));
        result.registerAdvancement();
        parser.advance();
        
        // Counter Identifier
        Token counterIdentifier = parser.getCurrentToken();
        if (counterIdentifier.type() != TokenType.IDENTIFIER) return result.failure(new SyntaxException(parser, "Expected variable identifier!"));
        result.registerAdvancement();
        parser.advance();
        
        // Assign Operator
        if (parser.getCurrentToken().type() != TokenType.ASSIGN) return result.failure(new SyntaxException(parser, "Expected '='!"));
        result.registerAdvancement();
        parser.advance();
        
        // Initial Value
        AbstractValuedNode initialValue = (AbstractValuedNode) result.register(GrammarRules.EXPRESSION.build(parser));
        if (result.error() != null) return result;
        
        // To Keyword
        if (parser.getCurrentToken().type() != TokenType.TO) return result.failure(new SyntaxException(parser, "Expected 'To'!"));
        result.registerAdvancement();
        parser.advance();
    
        // Initial Value
        AbstractValuedNode maxValue = (AbstractValuedNode) result.register(GrammarRules.EXPRESSION.build(parser));
        if (result.error() != null) return result;
        
        // Optional Step
        AbstractValuedNode step = null;
        if (parser.getCurrentToken().type() == TokenType.STEP)
        {
            result.registerAdvancement();
            parser.advance();
            step = (AbstractValuedNode) result.register(GrammarRules.EXPRESSION.build(parser));
        }
    
        // Newline
        if (parser.getCurrentToken().type() != TokenType.NEWLINE) return result.failure(new SyntaxException(parser, "Expected 'Newline'!"));
        result.registerAdvancement();
        parser.advance();
        
        // Body
        AbstractNode body = result.register(GrammarRules.block(parser, indentation + 1));
        if (result.error() != null) return result;
        
        // End Statement
        Result<Token> closeToken = result.registerIssues(GrammarRules.endStatement(parser, indentation, "For"));
        if (result.error() != null) return result;
        
        return result.success(new ForStatementNode(forKeyword, counterIdentifier, initialValue, maxValue, step, body, closeToken.get()));
    }
}
