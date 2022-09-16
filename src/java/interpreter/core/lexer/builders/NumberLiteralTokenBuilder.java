package interpreter.core.lexer.builders;

import interpreter.core.lexer.Token;
import interpreter.core.source.SourcePosition;

public class NumberLiteralTokenBuilder implements ITokenBuilder
{
    private static final String digits = "0123456789";
    private static final char radix = '.';
    
    private final Enum<?> integerType;
    private final Enum<?> realType;
    
    public NumberLiteralTokenBuilder(Enum<?> integerType, Enum<?> realType)
    {
        this.integerType = integerType;
        this.realType = realType;
    }
    
    @Override
    public int priority()
    {
        return -1;
    }
    
    @Override
    public Token tryBuild(SourcePosition position)
    {
        if (digits.indexOf(position.getCharacter()) < 0) return null;
        
        SourcePosition start = position.clone();
        StringBuilder numberBuilder = new StringBuilder(readDigits(position));
        int radixCount = 0;
        
        while (position.getCharacter() == radix)
        {
            radixCount++;
            position.advance();
            String digits = readDigits(position);
            
            if (digits.length() > 0) numberBuilder.append('.').append(digits);
            else break;
        }
        
        if (radixCount == 0) return new Token(integerType, Long.parseLong(numberBuilder.toString()), start, position);
        else if (radixCount == 1) return new Token(realType, Double.parseDouble(numberBuilder.toString()), start, position);
        else return null;
    }
    
    private String readDigits(SourcePosition position)
    {
        StringBuilder builder = new StringBuilder();
        while (digits.indexOf(position.getCharacter()) >= 0)
        {
            builder.append(position.getCharacter());
            position.advance();
        }
        return builder.toString();
    }
}
