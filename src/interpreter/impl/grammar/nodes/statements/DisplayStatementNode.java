package interpreter.impl.grammar.nodes.statements;

import interpreter.core.Interpreter;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.utils.Printing;

import java.util.function.BiConsumer;

public class DisplayStatementNode extends AbstractNode
{
    private final AbstractValuedNode message;
    
    public DisplayStatementNode(Token keyword, AbstractValuedNode message)
    {
        super(keyword.start(), message.end());
        this.message = message;
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer)
    {
        parentChildConsumer.accept(this, message);
        message.walk(parentChildConsumer);
    }
    @Override
    public void debugPrint(int depth)
    {
        Printing.Debug.print("  ".repeat(depth));
        Printing.Debug.println("Display:");
        message.debugPrint(depth + 1);
    }
    @Override
    public void interpret(Interpreter interpreter)
    {
        Printing.Output.println(message.getValue(interpreter));
    }
}
