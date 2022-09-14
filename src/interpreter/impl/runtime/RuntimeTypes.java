package interpreter.impl.runtime;

import interpreter.core.runtime.RuntimeType;
import interpreter.core.utils.Printing;

import java.util.Optional;

public class RuntimeTypes
{
    public static final RuntimeType<String> STRING = new RuntimeType<>("String")
    {
        @Override
        public Optional<String> tryParse(String str)
        {
            return Optional.of(str);
        }
    };
    public static final RuntimeType<Long> INTEGER = new RuntimeType<>("Integer")
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
    };
    public static final RuntimeType<Double> REAL = new RuntimeType<>("Integer")
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
    };
}
