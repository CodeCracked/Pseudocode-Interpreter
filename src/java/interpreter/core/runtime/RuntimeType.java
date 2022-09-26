package interpreter.core.runtime;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.utils.Result;

import java.util.HashMap;
import java.util.Map;

public abstract class RuntimeType<T>
{
    private static final Map<String, RuntimeType<?>> keywordRegistry = new HashMap<>();
    private static final Map<Class<?>, RuntimeType<?>> classRegistry = new HashMap<>();
    
    public static <T> void registerType(RuntimeType<T> type)
    {
        keywordRegistry.put(type.keyword, type);
        classRegistry.put(type.valueClass, type);
    }
    public static Result<RuntimeType<?>> getTypeFromKeyword(String keyword)
    {
        if (keywordRegistry.containsKey(keyword)) return Result.of(keywordRegistry.get(keyword));
        else return Result.fail(new IllegalArgumentException("Cannot find runtime type for keyword '" + keyword + "'! Please contact Markus to get this fixed."));
    }
    public static Result<RuntimeType<?>> getTypeFromClass(Class<?> clazz)
    {
        if (classRegistry.containsKey(clazz)) return Result.of(classRegistry.get(clazz));
        else return Result.fail(new IllegalArgumentException("Cannot find runtime type for Java type '" + clazz.getSimpleName() + "'! Please contact Markus to get this fixed."));
    }
    
    public final String keyword;
    public final Class<T> valueClass;
    
    public RuntimeType(String keyword, Class<T> valueClass)
    {
        this.keyword = keyword;
        this.valueClass = valueClass;
    }
    
    public Result<Integer> compare(AbstractNode expressionNode, Object value, RuntimeType<?> otherType, Object otherValue)
    {
        return tryCompare(expressionNode, (T)value, otherType, otherValue);
    }
    public String display(Object value) { return toString((T)value); }
    
    protected String toString(T value) { return value.toString(); }
    public abstract Result<T> tryParse(String str);
    public abstract Result<T> tryCast(Object value);
    protected abstract Result<Integer> tryCompare(AbstractNode expressionNode, T value, RuntimeType<?> otherType, Object otherValue);
    
    protected Result<Integer> compareNotSupported(AbstractNode expressionNode, RuntimeType<?> otherType)
    {
        return Result.fail(new SyntaxException(expressionNode, "Comparison between " + keyword + " and " + otherType.keyword + " is not supported!"));
    }
}
