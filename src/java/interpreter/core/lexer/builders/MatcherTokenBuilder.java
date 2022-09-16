package interpreter.core.lexer.builders;

import interpreter.core.lexer.Token;
import interpreter.core.source.SourcePosition;

public class MatcherTokenBuilder implements ITokenBuilder
{
    private final Enum<?> type;
    private final int priority;
    private final String token;
    private final boolean storeContents;
    private final boolean trailingSpace;
    
    public MatcherTokenBuilder(Enum<?> type, int priority, String token)
    {
        this(type, priority, token, false, false);
    }
    public MatcherTokenBuilder(Enum<?> type, int priority, String token, boolean storeContents, boolean trailingSpace)
    {
        this.type = type;
        this.priority = priority;
        this.token = token;
        this.storeContents = storeContents;
        this.trailingSpace = trailingSpace;
    }
    
    @Override
    public int priority()
    {
        return priority;
    }
    
    @Override
    public Token tryBuild(SourcePosition position)
    {
        SourcePosition start = position.clone();
        for (char test : token.toCharArray())
        {
            if (!position.hasNext() || position.getCharacter() != test) return null;
            else position.advance();
        }
        
        if (!trailingSpace || position.getCharacter() == ' ') return new Token(type, storeContents ? token : null, start, position);
        else return null;
    }
}
