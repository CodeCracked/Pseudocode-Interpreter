package interpreter.core.runtime;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.utils.Result;

public class VariableSymbol extends Symbol
{
    private final RuntimeType<?> runtimeType;
    private Object value;
    private boolean initialized;
    
    public VariableSymbol(Enum<?> type, String name, RuntimeType<?> runtimeType)
    {
        super(type, name);
        this.runtimeType = runtimeType;
        this.initialized = false;
    }
    
    public boolean isInitialized() { return initialized; }
    public RuntimeType<?> getRuntimeType() { return runtimeType; }
    
    public Result<Object> getValue(AbstractNode caller)
    {
        if (initialized) return Result.of(value);
        else return Result.fail(new SyntaxException(caller, "Cannot get value of variable '" + name  + "' before it is initialized!"));
    }
    
    public Result<?> setValue(Object value)
    {
        Result<?> casted = this.runtimeType.tryCast(value);
        if (casted.error() == null)
        {
            this.value = casted.get();
            this.initialized = true;
        }
        return casted;
    }
}
