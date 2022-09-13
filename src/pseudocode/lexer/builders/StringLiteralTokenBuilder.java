package pseudocode.lexer.builders;

import pseudocode.lexer.token.Token;
import pseudocode.lexer.token.TokenType;
import pseudocode.source.SourcePosition;

public class StringLiteralTokenBuilder extends AbstractTokenBuilder
{
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
        
        return new Token(TokenType.STRING_LITERAL, contents.toString(), start, position);
    }
}
