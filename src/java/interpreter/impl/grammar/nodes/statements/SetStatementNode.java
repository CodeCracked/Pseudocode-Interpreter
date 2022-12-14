package interpreter.impl.grammar.nodes.statements;

import interpreter.core.Interpreter;
import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.runtime.VariableSymbol;
import interpreter.core.utils.IO;
import interpreter.core.utils.Result;
import interpreter.impl.runtime.SymbolType;

import java.util.function.BiConsumer;

public class SetStatementNode extends AbstractNode
{
    private final String identifier;
    private final AbstractValuedNode value;
    
    private VariableSymbol symbol;
    
    public SetStatementNode(Token keyword, Token identifier, AbstractValuedNode value)
    {
        super(keyword.start(), value.end());
        this.identifier = identifier.value().toString();
        this.value = value;
    }
    
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
        
        // Variable Symbol
        symbol = getSymbolTable().getSymbol(SymbolType.VARIABLE, identifier);
        if (symbol == null) return Result.fail(new SyntaxException(this, "Cannot find variable '" + identifier + "'! Are you sure it was spelled and capitalized correctly?"));
        
        // Argument
        result.register(value.populate(interpreter));
        if (result.error() != null) return result;
        
        return result.success(null);
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer)
    {
        parentChildConsumer.accept(this, value);
        value.walk(parentChildConsumer);
    }
    
    @Override
    public void debugPrint(int depth)
    {
        IO.Debug.print("  ".repeat(depth));
        IO.Debug.println("ASSIGN " + identifier + ":");
        value.debugPrint(depth + 1);
    }
    
    @Override
    public Result<Void> interpret(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();

        Result<Object> argumentValue = result.registerIssues(value.getValue(interpreter));
        if (result.error() != null) return result;

        result.registerIssues(symbol.setValue(argumentValue.get(), this));
        if (result.error() != null) return result;
        
        return result.success(null);
    }
}
