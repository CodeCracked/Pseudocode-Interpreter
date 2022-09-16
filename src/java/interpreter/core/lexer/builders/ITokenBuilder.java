package interpreter.core.lexer.builders;

import interpreter.core.lexer.Token;
import interpreter.core.source.SourcePosition;

public interface ITokenBuilder
{
    default Token build(SourcePosition position)
    {
        position.markPosition();
        Token token = tryBuild(position);
        if (token == null) position.revertPosition();
        else position.unmarkPosition();
        return token;
    }
    
    int priority();
    Token tryBuild(SourcePosition position);
}
