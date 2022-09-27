package interpreter.core.runtime;

public abstract class Symbol
{
    public final Enum<?> type;
    public final String name;
    
    public Symbol(Enum<?> type, String name)
    {
        this.type = type;
        this.name = name;
    }
}
