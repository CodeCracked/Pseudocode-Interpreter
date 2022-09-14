package interpreter.impl.runtime;

import interpreter.core.runtime.RuntimeType;
import interpreter.core.utils.Printing;

import java.util.Optional;
import java.util.Set;

public class RuntimeTypes
{
    public static final RuntimeType<String> STRING = new RuntimeType<>("String", String.class)
    {
        @Override
        public Optional<String> tryParse(String str)
        {
            return Optional.of(str);
        }
    
        @Override
        public Optional<String> tryCast(Object value) { return Optional.of(value != null ? value.toString() : ""); }
    };
    public static final RuntimeType<Long> INTEGER = new RuntimeType<>("Integer", Long.class)
    {
        @Override
        public Optional<Long> tryParse(String str)
        {
            try
            {
                long value = Long.parseLong(str);
                return Optional.of(value);
            }
            catch (Exception e)
            {
                Printing.Errors.println("Invalid Integer '" + str + "'!");
                return Optional.empty();
            }
        }
    
        @Override
        public Optional<Long> tryCast(Object value)
        {
            if (value instanceof Integer casted) return Optional.of((long)casted);
            else if (value instanceof Long casted) return Optional.of(casted);
            else return Optional.empty();
        }
    };
    public static final RuntimeType<Double> REAL = new RuntimeType<>("Real", Double.class)
    {
        @Override
        public Optional<Double> tryParse(String str)
        {
            try
            {
                double value = Double.parseDouble(str);
                return Optional.of(value);
            }
            catch (Exception e)
            {
                Printing.Errors.println("Invalid Real '" + str + "'!");
                return Optional.empty();
            }
        }
    
        @Override
        public Optional<Double> tryCast(Object value)
        {
            if (value instanceof Integer casted) return Optional.of((double)casted);
            else if (value instanceof Long casted) return Optional.of((double)casted);
            else if (value instanceof Float casted) return Optional.of((double)casted);
            else if (value instanceof Double casted) return Optional.of(casted);
            else return Optional.empty();
        }
    };
    
    public static final Set<RuntimeType<?>> ALL_TYPES = Set.of(
            STRING,
            INTEGER,
            REAL
    );
}
