package interpreter.impl.grammar.nodes.components;

import interpreter.core.Interpreter;
import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.runtime.RuntimeType;
import interpreter.core.runtime.VariableSymbol;
import interpreter.core.utils.IO;
import interpreter.core.utils.Result;
import interpreter.impl.runtime.SymbolType;

import java.util.function.BiConsumer;

public class ParameterNode extends AbstractNode
{
    private final Token dataTypeToken;
    private final Token identifierToken;
    private final String identifier;
    public final boolean passByReference;
    
    private RuntimeType<?> dataType;
    private VariableSymbol symbol;
    
    public ParameterNode(Token dataType, Token identifier, boolean passByReference)
    {
        super(dataType.start(), identifier.end());
        this.dataTypeToken = dataType;
        this.identifierToken = identifier;
        this.identifier = identifier.value().toString();
        this.passByReference = passByReference;
    }
    
    public VariableSymbol getVariableSymbol() { return symbol; }
    
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
        
        // Data Type
        Result<RuntimeType<?>> dataType = result.registerIssues(RuntimeType.getTypeFromKeyword(dataTypeToken.value().toString()));
        if (result.error() != null) return result;
        this.dataType = dataType.get();
        
        // Symbol
        symbol = new VariableSymbol(SymbolType.VARIABLE, identifier, this.dataType, false);
        if (!getSymbolTable().tryAddSymbol(symbol)) return result.failure(new SyntaxException(identifierToken, "Variable '" + identifier + "' already exists!"));
        
        return result.success(null);
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer) { }
    
    @Override
    public void debugPrint(int depth)
    {
        IO.Debug.print("  ".repeat(depth));
        IO.Debug.println("ARGUMENT:");
        IO.Debug.print("  ".repeat(depth + 1));
        IO.Debug.println("Type: " + dataType.keyword);
        IO.Debug.print("  ".repeat(depth + 1));
        IO.Debug.println("Identifier: " + identifier);
    }
    
    @Override
    public Result<Void> interpret(Interpreter interpreter)
    {
        return Result.of(null);
    }
}
