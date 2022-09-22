package interpreter.core.utils;

import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Contains wrapper methods for printing. Currently, these only redirect to System.out, but
 * will allow for easily printing to a Swing window in the future.
 */
public class IO
{
    private static final Scanner defaultInput = new Scanner(System.in);

    public static IPrinter Warnings = System.err::printf;
    public static IPrinter Errors = System.err::printf;
    public static IPrinter Output = System.out::printf;
    public static IPrinter Debug = System.out::printf;
    public static IInput Input = callback -> callback.accept(defaultInput.nextLine());
    
    public interface IPrinter
    {
        void printf(String format, Object... args);
        
        default void print(Object obj) { printf(obj.toString()); }
        default void println(Object obj) { printf(obj.toString() + System.lineSeparator()); }
        default void println() { printf(System.lineSeparator()); }
    }
    public interface IInput
    {
        void readLine(Consumer<String> callback);
    }
}