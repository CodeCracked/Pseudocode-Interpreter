package interpreter.core.lexer.builders;

import interpreter.core.lexer.Token;
import interpreter.core.source.SourcePosition;

public class IdentifierTokenBuilder implements ITokenBuilder
{
    private static final String startingCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String bodyCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    private final Enum<?> type;
    
    public IdentifierTokenBuilder(Enum<?> type)
    {
        this.type = type;
    }
    
    @Override
    public int priority()
    {
        return 500;
    }
    
    @Override
    public Token tryBuild(SourcePosition position)
    {
        if (startingCharacters.indexOf(position.getCharacter()) < 0) return null;
        else
        {
            SourcePosition start = position.clone();
            
            StringBuilder contentBuilder = new StringBuilder();
            contentBuilder.append(position.getCharacter());
            
            while (position.advance() && bodyCharacters.indexOf(position.getCharacter()) >= 0) contentBuilder.append(position.getCharacter());
            return new Token(type, contentBuilder.toString(), start, position);
        }
    }
}
