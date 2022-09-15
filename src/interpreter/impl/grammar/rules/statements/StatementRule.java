package interpreter.impl.grammar.rules.statements;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.utils.Result;
import interpreter.core.parser.Parser;
import interpreter.impl.grammar.rules.GrammarRules;
import interpreter.impl.tokens.TokenType;

public class StatementRule implements IGrammarRule
{
    @Override
    public Result<AbstractNode> build(Parser parser)
    {
        Result<AbstractNode> result = new Result<>();
    
        // Get Keyword
        Token keywordToken = parser.getCurrentToken();
        if (keywordToken.type() != TokenType.STATEMENT_KEYWORD) return result.failure(new SyntaxException(parser, "Expected statement keyword! Did you remember proper capitalization?"));
        String keyword = (String)keywordToken.value();
    
        // Build Appropriate Grammar Rule
        AbstractNode statementNode = null;
        switch (keyword)
        {
            case "Display":
                statementNode = result.register(GrammarRules.DISPLAY_STATEMENT.build(parser));
                break;
            case "Declare":
                statementNode = result.register(GrammarRules.DECLARE_STATEMENT.build(parser));
                break;
            case "Set":
                statementNode = result.register(GrammarRules.SET_STATEMENT.build(parser));
                break;
            case "Input":
                statementNode = result.register(GrammarRules.INPUT_STATEMENT.build(parser));
                break;
            default:
                result.failure(new SyntaxException(parser, "Trying to use " + keyword + " statement, but no grammar rule was defined! Please let Markus know so he can fix this issue."));
                break;
        }
        
        // Return Result
        if (result.error() != null) return result;
        return result.success(statementNode);
    }
}
