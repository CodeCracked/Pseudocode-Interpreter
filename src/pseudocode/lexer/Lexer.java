package pseudocode.lexer;

import pseudocode.lexer.builders.*;
import pseudocode.lexer.keywords.KeywordLists;
import pseudocode.lexer.token.Token;
import pseudocode.lexer.token.TokenType;
import pseudocode.source.SourcePosition;
import pseudocode.utils.Printing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Lexer
{
    private final List<AbstractTokenBuilder> tokenBuilders;
    
    public Lexer()
    {
        List<AbstractTokenBuilder> tokenBuilders = new ArrayList<>();
        
        tokenBuilders.add(new KeywordTokenBuilder(TokenType.STATEMENT_KEYWORD, 1, KeywordLists.statementKeywords));
        tokenBuilders.add(new IdentifierTokenBuilder());
        tokenBuilders.add(new StringLiteralTokenBuilder());
        tokenBuilders.add(new MatcherTokenBuilder(TokenType.NEWLINE, -1000, "\n", false));
        
        tokenBuilders.sort(Comparator.comparingInt(AbstractTokenBuilder::priority));
        this.tokenBuilders = Collections.unmodifiableList(tokenBuilders);
    }
    
    public List<Token> tokenize(SourcePosition position)
    {
        List<Token> tokens = new ArrayList<>();
        
        while (position.hasNext())
        {
            Token token = null;
            for (AbstractTokenBuilder builder : tokenBuilders)
            {
                token = builder.build(position);
                if (token != null) break;
            }
            
            if (token == null)
            {
                Printing.Errors.println("Unknown symbol '" + position.getRemainingLine() + "'!");
                return null;
            }
            else tokens.add(token);
        }
        
        return tokens;
    }
}
