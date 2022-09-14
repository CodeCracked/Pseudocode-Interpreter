package interpreter.impl.grammar.rules.blocks;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.ParseResult;
import interpreter.core.parser.Parser;
import interpreter.impl.grammar.nodes.blocks.BlockNode;
import interpreter.impl.grammar.rules.GrammarRules;
import interpreter.impl.tokens.TokenType;

import java.util.ArrayList;
import java.util.List;

public class BlockRule implements IGrammarRule
{
    @Override
    public ParseResult build(Parser parser)
    {
        ParseResult result = new ParseResult();
        List<AbstractNode> statements = new ArrayList<>();
        
        // Parse Required First Statement
        AbstractNode statement = result.register(GrammarRules.STATEMENT.build(parser));
        if (result.error() != null) return result;
        else statements.add(statement);
        
        // Parse Optional Additional Statements
        while (parser.getCurrentToken().type() != TokenType.EOF)
        {
            parser.markPosition();
            ParseResult statementResult = GrammarRules.STATEMENT.build(parser);
            
            if (statementResult.error() == null)
            {
                parser.unmarkPosition();
                statements.add(result.register(statementResult));
            }
            else
            {
                parser.revertPosition();
                return result.failure(statementResult.error());
            }
        }
        
        return result.success(new BlockNode(statements));
    }
}
