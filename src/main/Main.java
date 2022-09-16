package main;

import swing.InterpreterWindow;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main
{
    public static void main(String[] args)
    {
        InterpreterWindow window = new InterpreterWindow();
        window.show();
        
        if (args.length > 0)
        {
            for (String arg : args)
            {
                Path path = Paths.get(arg);
                if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) window.interpretFile(path);
            }
        }
    }
}