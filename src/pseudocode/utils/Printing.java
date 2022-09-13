package pseudocode.utils;

/**
 * Contains wrapper methods for printing. Currently, these only redirect to System.out, but
 * will allow for easily printing to a Swing window in the future.
 */
public class Printing
{
    public static class Errors
    {
        public static void print(Object message)
        {
            System.err.print(message);
        }
        public static void println(Object message)
        {
            System.err.println(message);
        }
        public static void printf(String format, Object... args)
        {
            System.err.printf(format, args);
        }
    }
    
    public static void print(Object message)
    {
        System.out.print(message);
    }
    public static void println(Object message)
    {
        System.out.println(message);
    }
    public static void printf(String format, Object... args)
    {
        System.out.printf(format, args);
    }
}