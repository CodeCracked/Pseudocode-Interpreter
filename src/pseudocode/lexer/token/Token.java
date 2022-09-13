package pseudocode.lexer.token;

import pseudocode.source.SourcePosition;

import java.util.Objects;

public class Token
{
    private final TokenType type;
    private final String content;
    private final SourcePosition start;
    private final SourcePosition end;
    private final int trailingSpaces;
    
    public Token(TokenType type, String content, SourcePosition start, SourcePosition end)
    {
        this.type = type;
        this.content = content;
        this.start = start;
        this.end = end.clone();
        this.end.retract();
        this.trailingSpaces = end.readTrailingSpaces();
    }
    
    public TokenType type() { return type; }
    public String content() { return content; }
    public SourcePosition start() { return start; }
    public SourcePosition end() { return end; }
    public int trailingSpaces() { return trailingSpaces; }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(type.name());
        
        StringBuilder properties = new StringBuilder();
        if (content != null) properties.append("content: " + content + " ");
        if (trailingSpaces > 0) properties.append("trailingSpaces: " + trailingSpaces + " ");
        
        String propertiesStr = properties.toString();
        if (propertiesStr.length() > 0)
        {
            builder.append('(');
            builder.append(propertiesStr.trim());
            builder.append(')');
        }
        return builder.toString();
    }
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return type == token.type && Objects.equals(content, token.content);
    }
    @Override
    public int hashCode()
    {
        return Objects.hash(type, content);
    }
}
