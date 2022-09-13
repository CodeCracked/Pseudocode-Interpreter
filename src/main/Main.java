package main;

import pseudocode.source.SourcePosition;
import pseudocode.source.SourceCollection;
import pseudocode.utils.Printing;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main
{
    public static void main(String[] args)
    {
        if (args.length > 0)
        {
            for (String arg : args)
            {
                Path path = Paths.get(arg);
                if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) interpretFile(path);
            }
        }
    }
    
    private static void interpretFile(Path filePath)
    {
        SourceCollection source = SourceCollection.createFromFile(filePath);
        SourcePosition position = new SourcePosition(source);
        
        do Printing.print(position.getCharacter());
        while(!position.advance());
    }
}