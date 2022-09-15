package interpreter.impl.runtime;

import interpreter.core.runtime.RuntimeType;
import interpreter.core.utils.Result;

import java.util.Set;

public class RuntimeTypes
{
    public static final RuntimeType<String> STRING = new RuntimeType<>("String", String.class)
    {
        @Override
        public Result<String> tryParse(String str)
        {
            return Result.of(str);
        }
    
        @Override
        public Result<String> tryCast(Object value) { return Result.of(value != null ? value.toString() : ""); }
    };
    public static final RuntimeType<Long> INTEGER = new RuntimeType<>("Integer", Long.class)
    {
        @Override
        public Result<Long> tryParse(String str)
        {
            try { return Result.of(Long.parseLong(str)); }
            catch (Exception e) { return Result.fail(new NumberFormatException("Invalid Integer '" + str + "'!")); }
        }
    
        @Override
        public Result<Long> tryCast(Object value)
        {
            if (value instanceof Integer casted) return Result.of((long)casted);
            else if (value instanceof Long casted) return Result.of(casted);
            else return Result.fail(new IllegalArgumentException("Cannot cast " + value.getClass().getSimpleName() + " to RuntimeType " + keyword));
        }
    };
    public static final RuntimeType<Double> REAL = new RuntimeType<>("Real", Double.class)
    {
        @Override
        public Result<Double> tryParse(String str)
        {
            try { return Result.of(Double.parseDouble(str)); }
            catch (Exception e) { return Result.fail(new NumberFormatException("Invalid Real '" + str + "'!")); }
        }
    
        @Override
        public Result<Double> tryCast(Object value)
        {
            if (value instanceof Integer casted) return Result.of((double)casted);
            else if (value instanceof Long casted) return Result.of((double)casted);
            else if (value instanceof Float casted) return Result.of((double)casted);
            else if (value instanceof Double casted) return Result.of(casted);
            else return Result.fail(new IllegalArgumentException("Cannot cast " + value.getClass().getSimpleName() + " to RuntimeType " + keyword));
        }
    };
    
    public static final Set<RuntimeType<?>> ALL_TYPES = Set.of(
            STRING,
            INTEGER,
            REAL
    );
}
