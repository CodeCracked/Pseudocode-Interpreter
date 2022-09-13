package main;

import pseudocode.lexer.Lexer;
import pseudocode.lexer.token.Token;
import pseudocode.source.SourceCollection;
import pseudocode.source.SourcePosition;
import pseudocode.utils.Printing;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
        if (source == null) return;
        SourcePosition position = new SourcePosition(source);
    
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.tokenize(position);
        
        for (Token token : tokens) Printing.println(token);
    }
}