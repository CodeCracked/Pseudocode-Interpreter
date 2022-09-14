package interpreter.impl.grammar.nodes.blocks;

import interpreter.core.Interpreter;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.utils.Result;

import java.util.List;
import java.util.function.BiConsumer;

public class BlockNode extends AbstractNode
{
    private final List<AbstractNode> statements;
    
    public BlockNode(List<AbstractNode> statements)
    {
        super(statements.get(0).start(), statements.get(statements.size() - 1).end());
        this.statements = statements;
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer)
    {
        for (AbstractNode statement : statements)
        {
            parentChildConsumer.accept(this, statement);
            statement.walk(parentChildConsumer);
        }
    }
    
    @Override
    public void debugPrint(int depth)
    {
        for (AbstractNode statement : statements) statement.debugPrint(depth);
    }
    
    @Override
    public Result<Void> interpret(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
        for (AbstractNode statement : statements)
        {
            result.register(statement.interpret(interpreter));
            if (result.error() != null) return result;
        }
        return result.success(null);
    }
}
