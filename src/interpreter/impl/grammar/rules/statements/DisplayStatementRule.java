package interpreter.impl.grammar.rules.statements;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.utils.Result;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.impl.grammar.nodes.statements.DisplayStatementNode;
import interpreter.impl.grammar.rules.GrammarRules;
import interpreter.impl.tokens.TokenType;

public class DisplayStatementRule implements IGrammarRule
{
    @Override
    public Result<AbstractNode> build(Parser parser)
    {
        Result<AbstractNode> result = new Result<>();
    
        // Keyword
        Token keyword = parser.getCurrentToken();
        if (!keyword.isKeyword(TokenType.STATEMENT_KEYWORD, "Display", 1)) return result.failure(new SyntaxException(parser, "Expected keyword 'Display'! Did you forget proper capitalization?"));
        result.registerAdvancement();
        parser.advance();
        
        // Message
        AbstractValuedNode message = (AbstractValuedNode)result.register(GrammarRules.ATOM.build(parser));
        if (result.error() != null) return result;
        
        // Newline
        Token newline = parser.getCurrentToken();
        if (newline.type() != TokenType.NEWLINE) return result.failure(new SyntaxException(parser, "Expected newline!"));
        result.registerAdvancement();
        parser.advance();
        
        return result.success(new DisplayStatementNode(keyword, message));
    }
}
