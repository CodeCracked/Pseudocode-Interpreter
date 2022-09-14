package interpreter.impl.grammar.nodes;

import interpreter.core.parser.AbstractNode;
import interpreter.core.utils.Printing;

import java.util.function.BiConsumer;

public class ProgramNode extends AbstractNode
{
    private final AbstractNode displayStatement;
    
    public ProgramNode(AbstractNode displayStatement)
    {
        super(displayStatement.start(), displayStatement.end());
        this.displayStatement = displayStatement;
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer)
    {
        parentChildConsumer.accept(this, displayStatement);
        displayStatement.walk(parentChildConsumer);
    }
    @Override
    public void debugPrint(int depth)
    {
        Printing.Debug.print("  ".repeat(depth));
        Printing.Debug.println("PROGRAM");
        displayStatement.debugPrint(depth + 1);
    }
}
