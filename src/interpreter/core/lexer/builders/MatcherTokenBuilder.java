package interpreter.core.lexer.builders;

import interpreter.core.lexer.Token;
import interpreter.core.source.SourcePosition;

public class MatcherTokenBuilder extends AbstractTokenBuilder
{
    private final Enum<?> type;
    private final int priority;
    private final String token;
    private final boolean storeContents;
    
    public MatcherTokenBuilder(Enum<?> type, int priority, String token, boolean storeContents)
    {
        this.type = type;
        this.priority = priority;
        this.token = token;
        this.storeContents = storeContents;
    }
    
    @Override
    public int priority()
    {
        return priority;
    }
    
    @Override
    protected Token tryBuild(SourcePosition position)
    {
        SourcePosition start = position.clone();
        for (char test : token.toCharArray())
        {
            if (!position.hasNext() || position.getCharacter() != test) return null;
            else position.advance();
        }
        return new Token(type, storeContents ? token : null, start, position);
    }
}
