package interpreter.impl.grammar.nodes.expressions;

import interpreter.core.Interpreter;
import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.runtime.RuntimeType;
import interpreter.core.runtime.VariableSymbol;
import interpreter.core.utils.IO;
import interpreter.core.utils.Result;
import interpreter.impl.runtime.SymbolType;

import java.util.function.BiConsumer;

public class VariableAccessNode extends AbstractValuedNode
{
    private final String identifier;
    
    private VariableSymbol symbol;
    
    public VariableAccessNode(Token identifier)
    {
        super(identifier.start(), identifier.end());
        this.identifier = (String)identifier.value();
    }
    
    public VariableSymbol getVariableSymbol() { return symbol; }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer) { }
    
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        symbol = getSymbolTable().getSymbol(SymbolType.VARIABLE, identifier);
        if (symbol == null) return Result.fail(new SyntaxException(this, "Cannot find variable '" + identifier + "'! Are you sure it was spelled and capitalized correctly?"));
        else return Result.of(null);
    }
    
    @Override
    public void debugPrint(int depth)
    {
        IO.Debug.print("  ".repeat(depth));
        IO.Debug.println("Access: " + identifier);
    }
    
    @Override
    public Result<Void> interpret(Interpreter interpreter) { return Result.of(null); }
    
    @Override
    public Result<RuntimeType<?>> getRuntimeType()
    {
        return Result.of(symbol.getRuntimeType());
    }
    
    @Override
    public Result<Object> getValue(Interpreter interpreter)
    {
        return symbol.getValue(this);
    }
}
