package interpreter.core.lexer;

import interpreter.core.source.SourcePosition;

import java.util.Objects;

public class Token
{
    private final Enum<?> type;
    private final Object value;
    private final SourcePosition start;
    private final SourcePosition end;
    private final int trailingSpaces;
    
    public Token(Enum<?> type, Object value, SourcePosition start, SourcePosition end)
    {
        this.type = type;
        this.value = value;
        this.start = start;
        this.end = end.clone();
        this.end.retract();
        this.trailingSpaces = end.readTrailingSpaces();
    }
    
    public Enum<?> type() { return type; }
    public Object value() { return value; }
    public SourcePosition start() { return start; }
    public SourcePosition end() { return end; }
    public int trailingSpaces() { return trailingSpaces; }
    
    public boolean isKeyword(Enum<?> keywordType, String keyword, int trailingSpaces)
    {
        return this.type == keywordType && this.value == keyword && this.trailingSpaces == trailingSpaces;
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(type.name());
        
        StringBuilder properties = new StringBuilder();
        if (value != null) properties.append("value: ").append(value).append(" ");
        if (trailingSpaces > 0) properties.append("trailingSpaces: ").append(trailingSpaces).append(" ");
        
        String propertiesStr = properties.toString();
        if (propertiesStr.length() > 0) builder.append('(').append(propertiesStr.trim()).append(')');
        return builder.toString();
    }
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return type == token.type && Objects.equals(value, token.value);
    }
    @Override
    public int hashCode()
    {
        return Objects.hash(type, value);
    }
}
