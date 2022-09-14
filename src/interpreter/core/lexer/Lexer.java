package interpreter.core.lexer;

import interpreter.core.lexer.builders.AbstractTokenBuilder;
import interpreter.core.source.SourcePosition;
import interpreter.core.utils.Printing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Lexer
{
    private final List<AbstractTokenBuilder> tokenBuilders;
    
    public Lexer(AbstractTokenBuilder... builders)
    {
        List<AbstractTokenBuilder> tokenBuilders = new ArrayList<>();
        Collections.addAll(tokenBuilders, builders);
        tokenBuilders.sort(Comparator.comparingInt(AbstractTokenBuilder::priority));
        this.tokenBuilders = Collections.unmodifiableList(tokenBuilders);
    }
    
    public List<Token> tokenize(SourcePosition position, Enum<?> eofToken)
    {
        List<Token> tokens = new ArrayList<>();
        
        while (position.hasNext())
        {
            Token token = null;
            for (AbstractTokenBuilder builder : tokenBuilders)
            {
                token = builder.build(position);
                if (token != null) break;
            }
            
            if (token == null)
            {
                Printing.Errors.println("Unknown symbol '" + position.getRemainingLine() + "'!");
                return null;
            }
            else tokens.add(token);
        }
        
        if (eofToken != null) tokens.add(new Token(eofToken, null, position, position));
        return tokens;
    }
}
