package interpreter.core.lexer;

import interpreter.core.lexer.builders.ITokenBuilder;
import interpreter.core.source.SourcePosition;
import interpreter.core.utils.Printing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Lexer
{
    private final Enum<?> eofToken;
    private final List<ITokenBuilder> tokenBuilders;
    
    public Lexer(Enum<?> eofToken, ITokenBuilder... builders)
    {
        this.eofToken = eofToken;
        
        List<ITokenBuilder> tokenBuilders = new ArrayList<>();
        Collections.addAll(tokenBuilders, builders);
        tokenBuilders.sort(Comparator.comparingInt(ITokenBuilder::priority));
        this.tokenBuilders = Collections.unmodifiableList(tokenBuilders);
    }
    
    public List<Token> tokenize(SourcePosition position)
    {
        List<Token> tokens = new ArrayList<>();
        
        while (position.hasNext())
        {
            Token token = null;
            for (ITokenBuilder builder : tokenBuilders)
            {
                token = builder.build(position);
                if (token != null) break;
            }
            
            if (token == null)
            {
                Printing.Errors.println(position + ": Unknown symbol '" + position.getRemainingLine().trim() + "'!");
                return null;
            }
            else tokens.add(token);
        }
        
        if (eofToken != null) tokens.add(new Token(eofToken, null, position, position));
        return tokens;
    }
}
