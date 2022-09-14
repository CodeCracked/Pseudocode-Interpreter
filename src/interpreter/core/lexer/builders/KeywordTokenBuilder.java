package interpreter.core.lexer.builders;

import interpreter.core.lexer.Token;
import interpreter.core.source.SourcePosition;

import java.util.List;

public class KeywordTokenBuilder extends AbstractTokenBuilder
{
    private final int priority;
    private final MatcherTokenBuilder[] matchers;
    
    public KeywordTokenBuilder(Enum<?> type, int priority, List<String> keywords)
    {
        this.priority = priority;
        this.matchers = new MatcherTokenBuilder[keywords.size()];
        for (int i = 0; i < matchers.length; i++) matchers[i] = new MatcherTokenBuilder(type, priority, keywords.get(i), true);
    }
    
    @Override
    public int priority()
    {
        return priority;
    }
    
    @Override
    protected Token tryBuild(SourcePosition position)
    {
        Token token = null;
        for (MatcherTokenBuilder matcher : matchers)
        {
            token = matcher.build(position);
            if (token != null) break;
        }
        return token;
    }
}
