package swing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;

public class ColoredTextPane extends JTextPane
{
    public ColoredTextPane()
    {
        EmptyBorder eb = new EmptyBorder(new Insets(10, 10, 10, 10));
        setBorder(eb);
        setMargin(new Insets(5, 5, 5, 5));
    }
    
    public void append(Color color, String format, Object... args)
    {
        boolean editable = isEditable();
        setEditable(true);
        
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
    
        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Courier New");
        aset = sc.addAttribute(aset, StyleConstants.Size, 16);
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        
        int len = getDocument().getLength();
        setCaretPosition(len);
        setCharacterAttributes(aset, false);
        replaceSelection(String.format(format, args));
        setCaretPosition(0);
        
        setEditable(editable);
    }
}