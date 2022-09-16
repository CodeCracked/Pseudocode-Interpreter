package interpreter.core.parser.nodes;

import interpreter.core.runtime.RuntimeType;
import interpreter.core.source.SourcePosition;
import interpreter.core.utils.Result;

public abstract class AbstractTypedNode extends AbstractNode
{
    public AbstractTypedNode(SourcePosition startPosition, SourcePosition endPosition)
    {
        super(startPosition, endPosition);
    }
    
    public abstract Result<RuntimeType<?>> getRuntimeType();
}
