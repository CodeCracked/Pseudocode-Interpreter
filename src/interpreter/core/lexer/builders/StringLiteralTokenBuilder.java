package interpreter.core.lexer.builders;

import interpreter.core.lexer.Token;
import interpreter.core.source.SourcePosition;

public class StringLiteralTokenBuilder extends AbstractTokenBuilder
{
    private final Enum<?> type;
    
    public StringLiteralTokenBuilder(Enum<?> type)
    {
        this.type = type;
    }
    
    @Override
    public int priority()
    {
        return 0;
    }
    
    @Override
    public Token tryBuild(SourcePosition position)
    {
        if (position.getCharacter() != '"') return null;
        
        SourcePosition start = position.clone();
        StringBuilder contents = new StringBuilder();
        while (position.advance() && position.getCharacter() != '"') contents.append(position.getCharacter());
        
        if (position.getCharacter() != '"') return null;
        position.advance();
        
        return new Token(type, contents.toString(), start, position);
    }
}
