package swing;

import interpreter.core.utils.IO;
import interpreter.impl.PseudocodeInterpreter;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

public class InterpreterWindow
{
    private final PseudocodeInterpreter interpreter;
    private final JFrame frame;
    private final ColoredTextPane output;
    
    public InterpreterWindow()
    {
        interpreter = new PseudocodeInterpreter();
        
        frame = new JFrame("Pseudocode Interpreter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.setLocationRelativeTo(null);
        
        output = new ColoredTextPane();
        output.setBackground(Color.black);
        output.setEditable(false);
        
        frame.add(new JScrollPane(output));
    }
    
    public void show()
    {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) { e.printStackTrace(); }
        
        frame.setVisible(true);
    }
    public void interpretFile(Path filePath)
    {
        output.setText("");
    
        IO.Output = (format, args) -> output.append(Color.white, format, args);
        IO.Debug = (format, args) -> output.append(Color.gray, format, args);
        IO.Errors = (format, args) -> output.append(Color.red, format, args);;
        interpreter.runFile(filePath);
    }
}
