package interpreter.core.source;

import interpreter.core.utils.IO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SourceCollection
{
    public final List<CodeLine> lines;
    public final String commentStartRegex;
    
    private SourceCollection(List<CodeLine> lines, String commentStartRegex)
    {
        this.lines = Collections.unmodifiableList(lines);
        this.commentStartRegex = commentStartRegex;
    }
    
    public static SourceCollection createFromFile(Path path, String commentStartRegex)
    {
        List<String> lines;
        List<CodeLine> codeLines = new ArrayList<>();
        
        // Try to read file contents
        try { lines = Files.readAllLines(path); }
        catch (IOException e)
        {
            IO.Errors.printf("Failed to create pseudocode source from file path '%s'! Error: %s", path.toString(), e.getMessage());
            e.printStackTrace();
            return null;
        }
        
        // Create source lines from file contents
        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++)
        {
            // Get next line and strip comments
            String line = lines.get(lineNumber).split(commentStartRegex, 2)[0];
            if (line.trim().length() == 0) continue;
        
            // Trim off any whitespace at the end of the line, and append a newline character
            String endTrimmed = line.replaceAll("\\s+$", "") + '\n';
        
            // Create CodeLine and add to line list
            CodeLine codeLine = new CodeLine(endTrimmed, lineNumber + 1);
            codeLines.add(codeLine);
        }
        
        // Create and return PseudocodeSource
        return new SourceCollection(codeLines, commentStartRegex);
    }
}