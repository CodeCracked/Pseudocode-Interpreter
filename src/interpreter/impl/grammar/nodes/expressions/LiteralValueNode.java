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
    private final RuntimeType<?> runtimeType;
    private final Object value;
    
    private LiteralValueNode(Token token, RuntimeType<?> type, Object value)
    {
        super(token.start(), token.end());
        this.runtimeType = type;
        this.value = value;
    }
    
    public static Result<LiteralValueNode> create(Token value)
    {
        Result<LiteralValueNode> result = new Result<>();
        
        Result<RuntimeType<?>> runtimeType = RuntimeType.getTypeFromClass(value.value().getClass());
        if (runtimeType.error() != null) return result.failure(runtimeType.error());
        
        Result<?> casted = runtimeType.get().tryCast(value.value());
        if (casted.error() != null) return result.failure(casted.error());
        
        return result.success(new LiteralValueNode(value, runtimeType.get(), casted.get()));
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer) { }
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
