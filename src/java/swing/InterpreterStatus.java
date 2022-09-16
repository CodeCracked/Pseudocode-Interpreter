package swing;

import java.awt.*;

public enum InterpreterStatus
{
    PROCESSING("Processing", new Color(187, 0, 0)),
    WAITING_FOR_INPUT("Waiting for Input", new Color(187, 128, 0)),
    IDLE("Program Finished", new Color(0, 187, 0));
    
    public final String status;
    public final Color background;
    public final Color foreground;
    
    InterpreterStatus(String status, Color background)
    {
        this(status, background, Color.black);
    }
    InterpreterStatus(String status, Color background, Color foreground)
    {
        this.status = status;
        this.background = background;
        this.foreground = foreground;
    }
}
