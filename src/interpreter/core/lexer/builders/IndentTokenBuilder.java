package interpreter.core.lexer.builders;

import interpreter.core.lexer.Token;
import interpreter.core.source.SourcePosition;

public class IndentTokenBuilder implements ITokenBuilder
{
    private final Enum<?> type;
    private final int spacesPerIndent;
    
    public IndentTokenBuilder(Enum<?> type, int spacesPerIndent)
    {
        this.type = type;
        this.spacesPerIndent = spacesPerIndent;
    }
    
    @Override
    public int priority()
    {
        return -1000;
    }
    
    @Override
    public Token tryBuild(SourcePosition position)
    {
        if (position.getColumn() != 0) return null;
        
        SourcePosition start = position.clone();
        int indentation = 0;
        
        while (true)
        {
            if (position.getCharacter() == '\t')
            {
                indentation++;
                position.advance();
            }
            else if (position.getCharacter() == ' ')
            {
                int spaces = 0;
                while (position.getCharacter() == ' ')
                {
                    spaces++;
                    position.advance();
                }
                
                if (spaces % spacesPerIndent != 0) return null;
                else indentation += spaces / spacesPerIndent;
            }
            else break;
        }
        
        if (indentation > 0) return new Token(type, indentation, start, position);
        else return null;
    }
}
