package interpreter.core.utils;

import java.util.Scanner;

/**
 * Contains wrapper methods for printing. Currently, these only redirect to System.out, but
 * will allow for easily printing to a Swing window in the future.
 */
public class IO
{
    private static final Scanner defaultInput = new Scanner(System.in);
    
    public static IPrinter Errors = System.err::printf;
    public static IPrinter Output = System.out::printf;
    public static IPrinter Debug = (format, args) -> System.out.printf("\033[37;0m" + format + "\033[0m", args);
    public static IInput Input = defaultInput::nextLine;
    
    public interface IPrinter
    {
        void printf(String format, Object... args);
        
        default void print(Object obj) { printf(obj.toString()); }
        default void println(Object obj) { printf(obj.toString() + System.lineSeparator()); }
        default void println() { printf(System.lineSeparator()); }
    }
    public interface IInput
    {
        String readLine();
    }
}