package interpreter.core.lexer;

import interpreter.core.lexer.builders.ITokenBuilder;
import interpreter.core.source.SourcePosition;
import interpreter.core.utils.Result;

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
    
    public Result<List<Token>> tokenize(SourcePosition position)
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
            
            if (token == null) return Result.fail(new IllegalStateException(position + ": Unknown symbol '" + position.getRemainingLine().trim() + "'!"));
            else tokens.add(token);
        }
        
        if (eofToken != null) tokens.add(new Token(eofToken, null, position, position));
        return Result.of(tokens);
    }
}
