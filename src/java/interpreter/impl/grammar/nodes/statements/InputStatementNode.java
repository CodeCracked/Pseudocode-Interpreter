package interpreter.impl.grammar.nodes.statements;

import interpreter.core.Interpreter;
import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.runtime.VariableSymbol;
import interpreter.core.utils.IO;
import interpreter.core.utils.Result;
import interpreter.impl.runtime.SymbolType;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

public class InputStatementNode extends AbstractNode
{
    private final String identifier;
    
    private VariableSymbol symbol;
    
    public InputStatementNode(Token keyword, Token identifier)
    {
        super(keyword.start(), identifier.end());
        this.identifier = identifier.value().toString();
    }
    
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
        
        // Variable Symbol
        symbol = getSymbolTable().getSymbol(SymbolType.VARIABLE, identifier);
        if (symbol == null) return Result.fail(new SyntaxException(this, "Cannot find variable '" + identifier + "'! Are you sure it was spelled and capitalized correctly?"));
        
        return result.success(null);
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer) { }
    
    @Override
    public void debugPrint(int depth)
    {
        IO.Debug.print("  ".repeat(depth));
        IO.Debug.println("INPUT " + identifier + "");
    }
    
    @Override
    public Result<Void> interpret(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();

        // Get Input Line
        AtomicBoolean finishedFlag = new AtomicBoolean(false);
        AtomicReference<String> line = new AtomicReference<>();
        IO.Input.readLine(input ->
        {
            line.set(input);
            finishedFlag.set(true);
        });
        while (!finishedFlag.get()) Thread.onSpinWait();

        Result<?> parsed = result.registerIssues(symbol.getRuntimeType().tryParse(line.get()));
        if (result.error() != null) return result;

        result.registerIssues(symbol.setValue(parsed.get(), this));
        if (result.error() != null) return result;
        
        return result.success(null);
    }
}
