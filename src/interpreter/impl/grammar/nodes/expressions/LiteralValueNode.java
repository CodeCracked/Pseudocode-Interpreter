package interpreter.impl.grammar.nodes.expressions;

import interpreter.core.Interpreter;
import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.runtime.RuntimeType;
import interpreter.core.utils.Printing;
import interpreter.impl.runtime.RuntimeTypes;

import java.util.function.BiConsumer;

public class LiteralValueNode extends AbstractValuedNode
{
    private final Object value;
    private final RuntimeType<?> runtimeType;
    
    public LiteralValueNode(Token value)
    {
        super(value.start(), value.end());
        this.value = value.value();
        
        if (value.value() instanceof String) this.runtimeType = RuntimeTypes.STRING;
        else if (value.value() instanceof Long) this.runtimeType = RuntimeTypes.INTEGER;
        else if (value.value() instanceof Double) this.runtimeType = RuntimeTypes.REAL;
        else
        {
            Printing.Errors.println(new SyntaxException(this, "Cannot find runtime type for Java type '" + value.value().getClass().getSimpleName() + "'! Please contact Markus to get this fixed."));
            this.runtimeType = null;
        }
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer) { }
    @Override
    public void interpret(Interpreter interpreter) { }
    
    @Override
    public void debugPrint(int depth)
    {
        Printing.Debug.print("  ".repeat(depth));
        Printing.Debug.print(runtimeType.keyword);
        Printing.Debug.print(' ');
        Printing.Debug.println(value);
    }
    
    @Override
    public RuntimeType<?> getRuntimeType(Interpreter interpreter)
    {
        return this.runtimeType;
    }
    @Override
    public Object getValue(Interpreter interpreter)
    {
        return this.value;
    }
}
