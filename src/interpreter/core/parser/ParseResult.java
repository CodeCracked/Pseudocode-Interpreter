package interpreter.core.parser;

import interpreter.core.parser.nodes.AbstractNode;

public class ParseResult
{
    private AbstractNode node;
    private Exception error;
    private int advanceCount = 0;
    
    public ParseResult()
    {
        this.node = null;
        this.error = null;
    }
    
    public AbstractNode register(ParseResult result)
    {
        advanceCount += result.advanceCount;
        if (result.error != null) error = result.error;
        return result.node;
    }
    public AbstractNode registerAdvancement()
    {
        advanceCount++;
        return null;
    }
    public ParseResult success(AbstractNode node)
    {
        this.node = node;
        return this;
    }
    public ParseResult failure(Exception error)
    {
        if (this.error == null || advanceCount == 0) this.error = error;
        return this;
    }
    
    public AbstractNode node() { return node; }
    public Exception error() { return error; }
}
