package interpreter.impl.grammar.nodes.statements;

import interpreter.core.Interpreter;
import interpreter.core.lexer.Token;
import interpreter.core.parser.AbstractNode;
import interpreter.core.utils.Printing;

import java.util.function.BiConsumer;

public class DisplayStatementNode extends AbstractNode
{
    private final Token value;
    
    public DisplayStatementNode(Token keyword, Token value)
    {
        super(keyword.start(), value.end());
        this.value = value;
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer) { }
    @Override
    public void debugPrint(int depth)
    {
        Printing.Debug.print("  ".repeat(depth));
        Printing.Debug.printf("Display(%s)\n", value.value());
    }
    @Override
    public void interpret(Interpreter interpreter)
    {
        Printing.Output.println(value.value());
    }
}
