package interpreter.impl.grammar.nodes.statements;

import interpreter.core.Interpreter;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.utils.Printing;

import java.util.function.BiConsumer;

public class DeclareStatementNode extends AbstractNode
{
    private final Token dataType;
    private final String identifier;
    private final AbstractNode initialValue;
    
    public DeclareStatementNode(Token keyword, Token dataType, Token identifier, AbstractNode initialValue)
    {
        super(keyword.start(), initialValue != null ? initialValue.end() : identifier.end());
        this.dataType = dataType;
        this.identifier = (String)identifier.value();
        this.initialValue = initialValue;
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer)
    {
        parentChildConsumer.accept(this, initialValue);
        initialValue.walk(parentChildConsumer);
    }
    
    @Override
    public void debugPrint(int depth)
    {
        Printing.Debug.print("  ".repeat(depth));
        Printing.Debug.println("Declare " + dataType.value());
        
        Printing.Debug.print("  ".repeat(depth + 1));
        Printing.Debug.println("Identifier: " + identifier);
        
        Printing.Debug.print("  ".repeat(depth + 1));
        Printing.Debug.print("Initial Value: ");
        if (initialValue != null)
        {
            Printing.Debug.println();
            initialValue.debugPrint(depth + 2);
        }
        else Printing.Debug.println("None");
    }
    @Override
    public void interpret(Interpreter interpreter)
    {
        // TODO: Interpret Declaration Statement
    }
}
