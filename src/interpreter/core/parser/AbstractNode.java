package interpreter.core.parser;

import interpreter.core.Interpreter;
import interpreter.core.source.SourcePosition;

import java.nio.file.Path;
import java.util.function.BiConsumer;

public abstract class AbstractNode
{
    public AbstractNode parent;
    
    private final SourcePosition startPosition;
    private final SourcePosition endPosition;
    
    protected Path transpileTarget;
    
    public AbstractNode(SourcePosition startPosition, SourcePosition endPosition)
    {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }
    
    public abstract void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer);
    public abstract void debugPrint(int depth);
    public abstract void interpret(Interpreter interpreter);
    
    public SourcePosition start() { return this.startPosition; }
    public SourcePosition end() { return this.endPosition; }
    public Path transpileTarget() { return this.transpileTarget; }
}