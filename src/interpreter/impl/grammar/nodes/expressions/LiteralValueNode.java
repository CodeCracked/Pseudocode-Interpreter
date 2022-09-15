package interpreter.impl.grammar.nodes.expressions;

import interpreter.core.Interpreter;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.runtime.RuntimeType;
import interpreter.core.utils.Printing;
import interpreter.core.utils.Result;

import java.util.function.BiConsumer;

public class LiteralValueNode extends AbstractValuedNode
{
    private final Token valueToken;
    
    private RuntimeType<?> runtimeType;
    private Object value;
    
    public LiteralValueNode(Token value)
    {
        super(value.start(), value.end());
        this.valueToken = value;
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer) { }
    
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
    
        // Runtime Type
        Result<RuntimeType<?>> runtimeType = RuntimeType.getTypeFromClass(valueToken.value().getClass());
        if (runtimeType.error() != null) return result.failure(runtimeType.error());
        else this.runtimeType = runtimeType.get();
    
        // Value
        Result<?> casted = runtimeType.get().tryCast(valueToken.value());
        if (casted.error() != null) return result.failure(casted.error());
        else this.value = casted.get();
    
        return result.success(null);
    }
    
    @Override
    public Result<Void> interpret(Interpreter interpreter) { return Result.of(null); }
    
    @Override
    public void debugPrint(int depth)
    {
        Printing.Debug.print("  ".repeat(depth));
        Printing.Debug.print(runtimeType.keyword);
        Printing.Debug.print(' ');
        Printing.Debug.println(value);
    }
    
    @Override
    public Result<RuntimeType<?>> getRuntimeType()
    {
        return Result.of(runtimeType);
    }
    @Override
    public Result<Object> getValue(Interpreter interpreter)
    {
        return Result.of(value);
    }
}
