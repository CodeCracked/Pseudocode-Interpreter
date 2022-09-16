package interpreter.impl.grammar.nodes.components;

import interpreter.core.Interpreter;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.utils.IO;
import interpreter.core.utils.Result;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class ArgumentListNode extends AbstractNode
{
    public final List<AbstractValuedNode> arguments;
    
    public ArgumentListNode(Token openToken, ValueSetNode arguments, Token closeToken)
    {
        super(openToken.start(), closeToken.end());
        this.arguments = arguments != null ? arguments.values : Collections.emptyList();
    }
    
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
        for (AbstractValuedNode argument : arguments)
        {
            result.register(argument.populate(interpreter));
            if (result.error() != null) return result;
        }
        return result.success(null);
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer)
    {
        for (AbstractValuedNode argument : arguments)
        {
            parentChildConsumer.accept(this, argument);
            argument.walk(parentChildConsumer);
        }
    }
    
    @Override
    public void debugPrint(int depth)
    {
        IO.Debug.print("  ".repeat(depth));
        IO.Debug.println("ARGUMENTS:");
        for (AbstractValuedNode argument : arguments) argument.debugPrint(depth + 1);
    }
    
    @Override
    public Result<Void> interpret(Interpreter interpreter)
    {
        return Result.of(null);
    }
}
