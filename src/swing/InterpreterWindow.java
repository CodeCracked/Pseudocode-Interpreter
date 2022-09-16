package swing;

import interpreter.core.utils.IO;
import interpreter.impl.PseudocodeInterpreter;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.nio.file.Path;

public class InterpreterWindow extends JFrame
{
    private JTextPane output;
    private JTextField input;
    private JButton openFileButton;
    private JTextField statusDisplay;
    private JPanel mainPanel;
    
    private PseudocodeInterpreter interpreter;
    
    public InterpreterWindow()
    {
        setContentPane(mainPanel);
        setTitle("Interpreter Window");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setStatus(InterpreterStatus.IDLE);
        
        this.interpreter = new PseudocodeInterpreter();
    }
    
    public void interpretFile(Path filePath)
    {
        IO.Output = (format, args) -> append(output, Color.white, format, args);
        IO.Debug = (format, args) -> append(output, Color.gray, format, args);
        IO.Errors = (format, args) -> append(output, Color.red, format, args);
        
        output.setText("");
        input.setText("");
        
        setStatus(InterpreterStatus.PROCESSING);
        this.interpreter.runFile(filePath);
        append(output, Color.green, "Program Finished!");
        setStatus(InterpreterStatus.IDLE);
    }
    
    private void append(JTextPane pane, Color color, String format, Object... args)
    {
        boolean editable = pane.isEditable();
        pane.setEditable(true);
        
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
        
        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Courier New");
        aset = sc.addAttribute(aset, StyleConstants.Size, 16);
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        
        int len = pane.getDocument().getLength();
        pane.setCaretPosition(len);
        pane.setCharacterAttributes(aset, false);
        pane.replaceSelection(String.format(format, args));
        pane.setCaretPosition(0);
    
        pane.setEditable(editable);
    }
    
    private void setStatus(InterpreterStatus status)
    {
        statusDisplay.setText(status.status);
        statusDisplay.setBackground(status.background);
        statusDisplay.setForeground(status.foreground);
    }
}
