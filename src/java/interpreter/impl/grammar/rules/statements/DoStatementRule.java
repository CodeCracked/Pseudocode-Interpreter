package interpreter.impl.grammar.rules.statements;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.statements.DoStatementNode;
import interpreter.impl.grammar.rules.GrammarRules;
import interpreter.impl.tokens.TokenType;

public class DoStatementRule implements IGrammarRule
{
    @Override
    public Result<AbstractNode> build(Parser parser)
    {
        Result<AbstractNode> result = new Result<>();
        int indentation = parser.getCurrentIndent();
        
        // Do Keyword
        Token doKeyword = parser.getCurrentToken();
        if (!doKeyword.isKeyword(TokenType.STATEMENT_KEYWORD, "Do")) return result.failure(new SyntaxException(parser, "Expected 'Do'!"));
        result.registerAdvancement();
        parser.advance();
    
        // Newline
        if (parser.getCurrentToken().type() != TokenType.NEWLINE) return result.failure(new SyntaxException(parser, "Expected newline!"));
        result.registerAdvancement();
        parser.advance();
    
        // Body
        AbstractNode body = result.register(GrammarRules.block(parser, indentation + 1));
        if (result.error() != null) return result;
        
        // Indent
        if (indentation > 0 && !parser.getCurrentToken().matches(TokenType.INDENT, indentation)) return result.failure(new SyntaxException(parser, "Expected indent of size " + indentation));
        result.registerAdvancement();
        parser.advance();
        
        // Condition Type
        Token conditionType = parser.getCurrentToken();
        if (conditionType.type() != TokenType.UNTIL && !conditionType.isKeyword(TokenType.STATEMENT_KEYWORD, "While")) return result.failure(new SyntaxException(parser, "Expected 'While' or 'Until'!"));
        result.registerAdvancement();
        parser.advance();
        
        // Condition
        AbstractValuedNode condition = (AbstractValuedNode) result.register(GrammarRules.EXPRESSION.build(parser));
        if (result.error() != null) return result;
    
        // Newline
        if (parser.getCurrentToken().type() != TokenType.NEWLINE) return result.failure(new SyntaxException(parser, "Expected newline!"));
        result.registerAdvancement();
        parser.advance();
        
        return result.success(new DoStatementNode(doKeyword, body, conditionType, condition));
    }
}
