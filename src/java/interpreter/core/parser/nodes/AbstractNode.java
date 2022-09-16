package interpreter.core.parser.nodes;

import interpreter.core.Interpreter;
import interpreter.core.runtime.SymbolTable;
import interpreter.core.source.SourcePosition;
import interpreter.core.utils.Result;

import java.util.function.BiConsumer;

public abstract class AbstractNode
{
    public AbstractNode parent;
    
    private final SourcePosition startPosition;
    private final SourcePosition endPosition;
    
    public AbstractNode(SourcePosition startPosition, SourcePosition endPosition)
    {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }
    
    public void createSymbolTable() { }
    public SymbolTable getSymbolTable() { return parent.getSymbolTable(); }
    
    public abstract Result<Void> populate(Interpreter interpreter);
    public abstract void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer);
    public abstract void debugPrint(int depth);
    public abstract Result<Void> interpret(Interpreter interpreter);
    
    public SourcePosition start() { return this.startPosition; }
    public SourcePosition end() { return this.endPosition; }
}