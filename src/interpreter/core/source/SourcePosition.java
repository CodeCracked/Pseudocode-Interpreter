package interpreter.core.source;

import interpreter.core.utils.Pair;
import interpreter.core.utils.Printing;

import java.util.Objects;
import java.util.Stack;

public class SourcePosition
{
    private final SourceCollection source;
    private int lineIndex;
    private CodeLine line;
    private int column;
    private char character;
    
    private final Stack<Pair<Integer, Integer>> revertStack;
    
    public SourcePosition(SourceCollection source)
    {
        this.source = source;
        this.lineIndex = 0;
        this.line = source.lines.get(lineIndex);
        this.column = 0;
        this.character = this.line.line().charAt(0);
        this.revertStack = new Stack<>();
    }
    
    /**
     * Advance the position one character
     * @return If false, the end of the {@link SourceCollection} was reached.
     */
    public boolean advance()
    {
        column++;
        
        // Go to new line if necessary, and check end-of-file
        if (column >= line.line().length())
        {
            column = 0;
            lineIndex++;
            
            if (lineIndex >= source.lines.size()) return false;
            else line = source.lines.get(lineIndex);
        }
    
        // Get current character
        character = line.line().charAt(column);
        return true;
    }
    public boolean retract()
    {
        column--;
        
        if (column < 0)
        {
            lineIndex--;
            if (lineIndex < 0) return false;
            else line = source.lines.get(lineIndex);
            column = line.line().length() - 1;
        }
        
        character = line.line().charAt(column);
        return true;
    }
    public int readTrailingSpaces()
    {
        int spaces = 0;
        while (character == ' ' && advance()) spaces++;
        return spaces;
    }
    public boolean hasNext()
    {
        return lineIndex < source.lines.size();
    }
    
    public SourcePosition clone()
    {
        SourcePosition clone = new SourcePosition(source);
        clone.lineIndex = lineIndex;
        clone.line = line;
        clone.column = column;
        clone.character = character;
        clone.revertStack.addAll(revertStack);
        return clone;
    }
    
    // region Position Returning
    public void markPosition()
    {
        revertStack.push(Pair.of(lineIndex, column));
    }
    public void unmarkPosition()
    {
        if (revertStack.size() == 0)
        {
            Printing.Errors.println("Trying to revert position of CodeLocation when no positions have been marked!");
            throw new IllegalStateException("Trying to revert position of CodeLocation when no positions have been marked!");
        }
        else this.revertStack.pop();
    }
    public void revertPosition()
    {
        if (revertStack.size() == 0)
        {
            Printing.Errors.println("Trying to revert position of CodeLocation when no positions have been marked!");
            throw new IllegalStateException("Trying to revert position of CodeLocation when no positions have been marked!");
        }
        else
        {
            Pair<Integer, Integer> position = revertStack.pop();
            this.lineIndex = position.left;
            this.column = position.right;
            this.line = source.lines.get(lineIndex);
            this.character = this.line.line().charAt(column);
        }
    }
    // endregion
    //region Getters
    public CodeLine getLine() { return line; }
    public int getLineNumber() { return line.lineNumber(); }
    public int getColumn() { return column; }
    public char getCharacter() { return character; }
    
    public String getRemainingLine()
    {
        StringBuilder builder = new StringBuilder();
        for (int i = column; i < line.line().length(); i++) builder.append(line.line().charAt(i));
        return builder.toString();
    }
    //endregion
    //region Object Overrides
    @Override
    public String toString()
    {
        return String.format("(Line: %d, Column: %d)", line.lineNumber(), column);
    }
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SourcePosition that = (SourcePosition) o;
        return column == that.column && line.equals(that.line);
    }
    @Override
    public int hashCode()
    {
        return Objects.hash(line, column);
    }
    //endregion
}
