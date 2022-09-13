package pseudocode.lexer.builders;

import pseudocode.lexer.token.Token;
import pseudocode.lexer.token.TokenType;
import pseudocode.source.SourcePosition;

public class IdentifierTokenBuilder extends AbstractTokenBuilder
{
    private static final String startingCharacters = "abcdefghijklmnopqrstuvwxyz";
    private static final String bodyCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    @Override
    public int priority()
    {
        return 0;
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
            return new Token(TokenType.IDENTIFIER, contentBuilder.toString(), start, position);
        }
    }
}
