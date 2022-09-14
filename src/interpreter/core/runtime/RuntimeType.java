package interpreter.core.runtime;

import java.util.Optional;

public abstract class RuntimeType<T>
{
    public final String keyword;
    
    public RuntimeType(String keyword)
    {
        this.keyword = keyword;
    }
    
    public abstract Optional<T> tryParse(String str);
}
