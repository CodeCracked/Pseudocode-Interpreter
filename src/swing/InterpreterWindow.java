package swing;

import interpreter.core.utils.IO;
import interpreter.impl.PseudocodeInterpreter;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.function.Consumer;

public class InterpreterWindow extends JFrame implements IO.IInput
{
    private JTextPane output;
    private JTextField input;
    private JButton openFileButton;
    private JTextField statusDisplay;
    private JPanel mainPanel;
    
    private final PseudocodeInterpreter interpreter;
    private Consumer<String> inputCallback;
    
    public InterpreterWindow()
    {
        setContentPane(mainPanel);
        setTitle("Interpreter Window");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setStatus(InterpreterStatus.IDLE);
        
        this.interpreter = new PseudocodeInterpreter();
        openFileButton.addActionListener(e -> promptOpenFile());
        input.setCaretColor(new Color(0, 187, 0));
        input.addActionListener(e ->
        {
            setStatus(InterpreterStatus.PROCESSING);
            String line = input.getText().substring(2);
            append(output, input.getCaretColor(), line + System.lineSeparator());
            input.setText("");
            input.setEditable(false);
            inputCallback.accept(line);
        });
        input.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                if (input.getText().length() > 0 && !input.getText().startsWith("> ")) input.setText("> " + input.getText().substring(1));
            }
        });
    }
    
    public void interpretFile(Path filePath)
    {
        IO.Output = (format, args) -> append(output, Color.white, format, args);
        IO.Debug = (format, args) -> append(output, Color.gray, format, args);
        IO.Errors = (format, args) -> append(output, Color.red, format, args);
        IO.Input = this;
        
        output.setText("");
        input.setText("");
        
        setStatus(InterpreterStatus.PROCESSING);
        this.interpreter.runFile(filePath, successful ->
        {
            if (successful) append(output, Color.green, "Program Finished!");
            else append(output, Color.red, "Program Failed!");
            setStatus(InterpreterStatus.IDLE);
        });
    }
    
    private void promptOpenFile()
    {
        final JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) interpretFile(fileChooser.getSelectedFile().toPath());
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
    
    @Override
    public void readLine(Consumer<String> callback)
    {
        setStatus(InterpreterStatus.WAITING_FOR_INPUT);
        this.inputCallback = callback;
        input.setText("> ");
        input.select(2, 2);
        input.setEditable(true);
        input.grabFocus();
    }
}
