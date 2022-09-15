package interpreter.impl.grammar.nodes.components;

import interpreter.core.Interpreter;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.utils.Printing;
import interpreter.core.utils.Result;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class ParameterListNode extends AbstractNode
{
    public final List<ParameterNode> parameters;
    
    public ParameterListNode(Token leftParenthesis, List<ParameterNode> parameters, Token rightParenthesis)
    {
        super(leftParenthesis.start(), rightParenthesis.end());
        this.parameters = Collections.unmodifiableList(parameters);
    }
    
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
        for (ParameterNode arg : parameters)
        {
            result.register(arg.populate(interpreter));
            if (result.error() != null) return result;
        }
        return result.success(null);
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer)
    {
        for (ParameterNode arg : parameters)
        {
            parentChildConsumer.accept(this, arg);
            arg.walk(parentChildConsumer);
        }
    }
    
    @Override
    public void debugPrint(int depth)
    {
        Printing.Debug.print("  ".repeat(depth));
        Printing.Debug.println("ARGUMENTS:");
        for (ParameterNode arg : parameters) arg.debugPrint(depth + 1);
    }
    
    @Override
    public Result<Void> interpret(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
        for (ParameterNode arg : parameters)
        {
            result.register(arg.interpret(interpreter));
            if (result.error() != null) return result;
        }
        return result.success(null);
    }
}
