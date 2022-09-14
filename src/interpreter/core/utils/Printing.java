package interpreter.core.utils;

/**
 * Contains wrapper methods for printing. Currently, these only redirect to System.out, but
 * will allow for easily printing to a Swing window in the future.
 */
public class Printing
{
    public static final IPrinter Errors = System.err::printf;
    public static final IPrinter Output = System.out::printf;
    public static final IPrinter Debug = (format, args) -> System.out.printf("\033[37;0m" + format + "\033[0m", args);
    
    public interface IPrinter
    {
        void printf(String format, Object... args);
        
        default void print(Object obj) { printf(obj.toString()); }
        default void println(Object obj) { printf(obj.toString() + '\n'); }
        default void println() { printf("\n"); }
    }
}