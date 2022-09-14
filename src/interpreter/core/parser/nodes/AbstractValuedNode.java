package interpreter.core.parser.nodes;

import interpreter.core.Interpreter;
import interpreter.core.source.SourcePosition;

public abstract class AbstractValuedNode extends AbstractTypedNode
{
    public AbstractValuedNode(SourcePosition startPosition, SourcePosition endPosition)
    {
        super(startPosition, endPosition);
    }
    
    public abstract Object getValue(Interpreter interpreter);
}
