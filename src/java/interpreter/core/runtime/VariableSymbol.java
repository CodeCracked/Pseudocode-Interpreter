package interpreter.core.runtime;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.utils.Result;

public class VariableSymbol extends Symbol
{
    private final RuntimeType<?> runtimeType;
    private final boolean constant;
    private Object value;
    private boolean initialized;
    
    public VariableSymbol(Enum<?> type, String name, RuntimeType<?> runtimeType, boolean constant)
    {
        this(type, name, runtimeType, null, constant);
    }
    public VariableSymbol(Enum<?> type, String name, RuntimeType<?> runtimeType, Object value, boolean constant)
    {
        super(type, name);
        this.runtimeType = runtimeType;
        this.constant = constant;
        if (value != null)
        {
            this.value = value;
            this.initialized = true;
        }
    }
    
    @Override
    public Symbol clone()
    {
        VariableSymbol clone = new VariableSymbol(type, name, runtimeType, constant);
        clone.value = value;
        clone.initialized = initialized;
        return clone;
    }
    
    public boolean isInitialized() { return initialized; }
    public boolean isConstant() { return constant; }
    public RuntimeType<?> getRuntimeType() { return runtimeType; }
    
    public Result<Object> getValue(AbstractNode caller)
    {
        if (initialized) return Result.of(value);
        else return Result.fail(new SyntaxException(caller, "Cannot get value of variable '" + name  + "' before it is initialized!"));
    }
    public Result<?> setValue(Object value, AbstractNode assignmentNode)
    {
        if (constant && initialized) return Result.fail(new SyntaxException(assignmentNode, "Cannot change the value of constant '" + name + "'!"));
        
        Result<?> casted = this.runtimeType.tryCast(value);
        if (casted.error() == null)
        {
            this.value = casted.get();
            this.initialized = true;
        }
        return casted;
    }
}
