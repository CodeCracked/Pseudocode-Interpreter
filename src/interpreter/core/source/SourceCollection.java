package interpreter.core.source;

import interpreter.core.utils.Printing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SourceCollection
{
    public final List<CodeLine> lines;
    
    private SourceCollection(List<CodeLine> lines)
    {
        this.lines = Collections.unmodifiableList(lines);
    }
    
    public static SourceCollection createFromFile(Path path)
    {
        List<String> lines;
        List<CodeLine> codeLines = new ArrayList<>();
        
        // Try to read file contents
        try { lines = Files.readAllLines(path); }
        catch (IOException e)
        {
            Printing.Errors.printf("Failed to create pseudocode source from file path '%s'! Error: %s", path.toString(), e.getMessage());
            e.printStackTrace();
            return null;
        }
        
        // Create source lines from file contents
        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++)
        {
            // Get next line, and ignore if it is only whitespace
            String line = lines.get(lineNumber);
            if (line.trim().length() == 0) continue;
        
            // Trim off any whitespace at the end of the line, and append a newline character
            String endTrimmed = line.replaceAll("\\s+$", "") + '\n';
        
            // Create CodeLine and add to line list
            CodeLine codeLine = new CodeLine(endTrimmed, lineNumber + 1);
            codeLines.add(codeLine);
        }
        
        // Create and return PseudocodeSource
        return new SourceCollection(codeLines);
    }
}