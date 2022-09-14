package interpreter.core.runtime;

import java.util.Optional;

public class VariableSymbol extends Symbol
{
    private final RuntimeType<?> runtimeType;
    private Object value;
    private boolean initialized;
    
    public VariableSymbol(Enum<?> type, String name, RuntimeType<?> runtimeType, Optional<?> initialValue)
    {
        super(type, name);
        this.runtimeType = runtimeType;
        this.value = initialValue.orElse(null);
        this.initialized = initialValue.isPresent();
    }
    
    public boolean isInitialized() { return initialized; }
    public RuntimeType<?> getRuntimeType() { return runtimeType; }
    public Object getValue() { return value; }
    
    public boolean setValue(Object value)
    {
        Optional<?> casted = this.runtimeType.tryCast(value);
        if (casted.isPresent())
        {
            this.value = value;
            this.initialized = true;
            return true;
        }
        else return false;
    }
}
