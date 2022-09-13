package pseudocode.lexer.builders;

import pseudocode.lexer.token.Token;
import pseudocode.source.SourcePosition;

public abstract class AbstractTokenBuilder
{
    public Token build(SourcePosition position)
    {
        position.markPosition();
        Token token = tryBuild(position);
        if (token == null) position.revertPosition();
        else position.unmarkPosition();
        return token;
    }
    
    public abstract int priority();
    protected abstract Token tryBuild(SourcePosition position);
}
