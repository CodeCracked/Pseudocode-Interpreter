package interpreter.core.parser.nodes;

import interpreter.core.Interpreter;
import interpreter.core.source.SourcePosition;

import java.util.Optional;

public abstract class AbstractValuedNode extends AbstractTypedNode
{
    public AbstractValuedNode(SourcePosition startPosition, SourcePosition endPosition)
    {
        super(startPosition, endPosition);
    }
    
    public abstract Optional<?> getValue(Interpreter interpreter);
}
