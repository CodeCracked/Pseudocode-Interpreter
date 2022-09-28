package interpreter.impl.grammar.rules.statements;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.statements.WhileStatementNode;
import interpreter.impl.grammar.rules.GrammarRules;
import interpreter.impl.tokens.TokenType;

public class WhileStatementRule implements IGrammarRule
{
    @Override
    public Result<AbstractNode> build(Parser parser)
    {
        Result<AbstractNode> result = new Result<>();
        int indentation = parser.getCurrentIndent();
        
        // While Keyword
        Token whileKeyword = parser.getCurrentToken();
        if (!whileKeyword.isKeyword(TokenType.STATEMENT_KEYWORD, "While")) return result.failure(new SyntaxException(parser, "Expected 'While'!"));
        result.registerAdvancement();
        parser.advance();
        
        // Condition
        AbstractValuedNode conditionNode = (AbstractValuedNode) result.register(GrammarRules.EXPRESSION.build(parser));
        if (result.error() != null) return result;
        
        // Newline
        if (parser.getCurrentToken().type() != TokenType.NEWLINE) return result.failure(new SyntaxException(parser, "Expected newline!"));
        result.registerAdvancement();
        parser.advance();
        
        // Body
        AbstractNode body = result.register(GrammarRules.block(parser, indentation + 1));
        if (result.error() != null) return result;
        
        // End Statement
        Result<Token> closeToken = result.registerIssues(GrammarRules.endStatement(parser, indentation, "While"));
        if (result.error() != null) return result;
        
        return result.success(new WhileStatementNode(whileKeyword, conditionNode, body, closeToken.get()));
    }
}
