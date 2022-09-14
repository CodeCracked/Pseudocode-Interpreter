package interpreter.impl.grammar.nodes;

import interpreter.core.Interpreter;
import interpreter.core.parser.AbstractNode;
import interpreter.core.utils.Printing;

import java.util.function.BiConsumer;

public class ProgramNode extends AbstractNode
{
    private final AbstractNode block;
    
    public ProgramNode(AbstractNode block)
    {
        super(block.start(), block.end());
        this.block = block;
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer)
    {
        parentChildConsumer.accept(this, block);
        block.walk(parentChildConsumer);
    }
    @Override
    public void debugPrint(int depth)
    {
        Printing.Debug.print("  ".repeat(depth));
        Printing.Debug.println("PROGRAM");
        block.debugPrint(depth + 1);
    }
    @Override
    public void interpret(Interpreter interpreter)
    {
        block.interpret(interpreter);
    }
}
