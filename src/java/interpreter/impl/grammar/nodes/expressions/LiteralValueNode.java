package interpreter.impl.grammar.nodes.expressions;

import interpreter.core.Interpreter;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.runtime.RuntimeType;
import interpreter.core.utils.IO;
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
        Result<RuntimeType<?>> runtimeType = result.registerIssues(RuntimeType.getTypeFromClass(valueToken.value().getClass()));
        if (result.error() != null) return result;
        this.runtimeType = runtimeType.get();
    
        // Value
        Result<?> casted = result.registerIssues(runtimeType.get().tryCast(valueToken.value()));
        if (result.error() != null) return result;
        this.value = casted.get();
    
        return result.success(null);
    }
    
    @Override
    public Result<Void> interpret(Interpreter interpreter) { return Result.of(null); }
    
    @Override
    public void debugPrint(int depth)
    {
        IO.Debug.print("  ".repeat(depth));
        IO.Debug.print(runtimeType.keyword);
        IO.Debug.print(": ");
        IO.Debug.println(value);
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
