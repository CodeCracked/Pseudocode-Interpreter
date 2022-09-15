package interpreter.impl.grammar.nodes.components;

import interpreter.core.Interpreter;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.utils.Printing;
import interpreter.core.utils.Result;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class ValueSetNode extends AbstractNode
{
    public final List<AbstractValuedNode> values;
    
    public ValueSetNode(List<AbstractValuedNode> values)
    {
        super(values.get(0).start(), values.get(values.size() - 1).end());
        this.values = Collections.unmodifiableList(values);
    }
    
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
        for (AbstractValuedNode value : values)
        {
            result.register(value.populate(interpreter));
            if (result.error() != null) return result;
        }
        return result.success(null);
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer)
    {
        for (AbstractValuedNode value : values)
        {
            parentChildConsumer.accept(this, value);
            value.walk(parentChildConsumer);
        }
    }
    
    @Override
    public void debugPrint(int depth)
    {
        Printing.Debug.print("  ".repeat(depth));
        Printing.Debug.println("VALUE SET:");
        for (AbstractValuedNode value : values) value.debugPrint(depth + 1);
    }
    
    @Override
    public Result<Void> interpret(Interpreter interpreter)
    {
        return Result.of(null);
    }
}
