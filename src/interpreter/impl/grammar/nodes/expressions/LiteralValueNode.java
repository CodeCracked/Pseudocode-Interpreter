package interpreter.impl.grammar.nodes.expressions;

import interpreter.core.Interpreter;
import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.runtime.RuntimeType;
import interpreter.core.utils.Printing;

import java.util.Optional;
import java.util.function.BiConsumer;

public class LiteralValueNode extends AbstractValuedNode
{
    private final RuntimeType<?> runtimeType;
    private final Optional<?> value;
    
    public LiteralValueNode(Token value)
    {
        super(value.start(), value.end());
        
        Optional<RuntimeType<?>> dataType = RuntimeType.getTypeFromClass(value.value().getClass());
        if (dataType.isPresent())
        {
            this.runtimeType = dataType.get();
            this.value = this.runtimeType.tryCast(value.value());
        }
        else
        {
            this.runtimeType = null;
            this.value = Optional.empty();
            Printing.Errors.println(new SyntaxException(this, "Cannot find runtime type for Java type '" + value.value().getClass().getSimpleName() + "'! Please contact Markus to get this fixed."));
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
    public Optional<?> getValue(Interpreter interpreter)
    {
        return this.value;
    }
}
