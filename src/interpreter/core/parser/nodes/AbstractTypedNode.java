package interpreter.core.parser.nodes;

import interpreter.core.Interpreter;
import interpreter.core.runtime.RuntimeType;
import interpreter.core.source.SourcePosition;

public abstract class AbstractTypedNode extends AbstractNode
{
    public AbstractTypedNode(SourcePosition startPosition, SourcePosition endPosition)
    {
        super(startPosition, endPosition);
    }
    
    public abstract RuntimeType<?> getRuntimeType(Interpreter interpreter);
}
