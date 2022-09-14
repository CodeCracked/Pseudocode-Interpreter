package interpreter.core.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class RuntimeType<T>
{
    private static final Map<String, RuntimeType<?>> keywordRegistry = new HashMap<>();
    private static final Map<Class<?>, RuntimeType<?>> classRegistry = new HashMap<>();
    
    public static <T> void registerType(RuntimeType<T> type)
    {
        keywordRegistry.put(type.keyword, type);
        classRegistry.put(type.valueClass, type);
    }
    public static Optional<RuntimeType<?>> getTypeFromKeyword(String keyword)
    {
        if (keywordRegistry.containsKey(keyword)) return Optional.of(keywordRegistry.get(keyword));
        else return Optional.empty();
    }
    public static Optional<RuntimeType<?>> getTypeFromClass(Class<?> clazz)
    {
        if (classRegistry.containsKey(clazz)) return Optional.of(classRegistry.get(clazz));
        else return Optional.empty();
    }
    
    public final String keyword;
    public final Class<T> valueClass;
    
    public RuntimeType(String keyword, Class<T> valueClass)
    {
        this.keyword = keyword;
        this.valueClass = valueClass;
    }
    
    public abstract Optional<T> tryParse(String str);
    public abstract Optional<T> tryCast(Object value);
}
