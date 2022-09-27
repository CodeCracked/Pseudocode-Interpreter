package interpreter.impl.grammar.nodes.components;

import interpreter.core.Interpreter;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.runtime.SymbolTable;
import interpreter.core.utils.IO;
import interpreter.core.utils.Result;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class ParameterListNode extends AbstractNode
{
    public final List<ParameterNode> parameters;
    
    private SymbolTable parameterTable;
    
    public ParameterListNode(Token leftParenthesis, List<ParameterNode> parameters, Token rightParenthesis)
    {
        super(leftParenthesis.start(), rightParenthesis.end());
        this.parameters = Collections.unmodifiableList(parameters);
        for (ParameterNode parameter : this.parameters) parameter.setOwner(this);
    }
    
    public void setParameterTable(SymbolTable parameterTable)
    {
        this.parameterTable = parameterTable;
    }
    
    @Override
    public SymbolTable getSymbolTable()
    {
        return this.parameterTable;
    }
    
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        if (parameterTable == null) parameterTable = parent.getSymbolTable();
        
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
        IO.Debug.print("  ".repeat(depth));
        IO.Debug.println("ARGUMENTS:");
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
