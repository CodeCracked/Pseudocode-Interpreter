package interpreter.core.exceptions;

import interpreter.core.source.SourcePosition;

public class InterpreterException extends Exception
{
    protected final SourcePosition start;
    protected final SourcePosition end;
    protected final String errorName;
    protected final String details;
    
    public InterpreterException(SourcePosition start, SourcePosition end, String errorName, String details)
    {
        this.start = start;
        this.end = end;
        this.errorName = errorName;
        this.details = details;
    }
    
    @Override
    public String getMessage()
    {
        return toString();
    }
    
    private String getArrowsString(String line, int start, int end)
    {
        StringBuilder builder = new StringBuilder();
        
        char[] lineChars = line.toCharArray();
        for (int i = 0; i < start; i++)
        {
            if (lineChars[i] == '\t') builder.append("    ");
            else builder.append(' ');
        }
        
        for (int i = start; i < Math.min(end, lineChars.length); i++)
        {
            if (lineChars[i] == '\t') builder.append("^^^^");
            else builder.append('^');
        }
        builder.append("^".repeat(Math.max(0, end - Math.min(end, lineChars.length))));
        
        return builder.toString();
    }
    
    @Override
    public String toString()
    {
        return String.format("%s: %s\n", errorName, details) +
                String.format("Line %s, Column %s\n", start.getLineNumber(), start.getColumn()) +
                String.format("\n%s", start.getLine().line().replace("\t", "    ")) +
                getArrowsString(start.getLine().line(), start.getColumn(), end.getLineNumber() > start.getLineNumber() ? start.getColumn() + 1 : end.getColumn()) +
                '\n';
    }
}
