package interpreter.impl.grammar.nodes.components;

import interpreter.core.Interpreter;
import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.runtime.RuntimeType;
import interpreter.core.runtime.VariableSymbol;
import interpreter.core.utils.Printing;
import interpreter.core.utils.Result;
import interpreter.impl.runtime.SymbolType;

import java.util.function.BiConsumer;

public class ParameterNode extends AbstractNode
{
    private final Token dataTypeToken;
    private final Token identifierToken;
    private final String identifier;
    
    private RuntimeType<?> dataType;
    private VariableSymbol symbol;
    
    public ParameterNode(Token dataType, Token identifier)
    {
        super(dataType.start(), identifier.end());
        this.dataTypeToken = dataType;
        this.identifierToken = identifier;
        this.identifier = identifier.value().toString();
    }
    
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
        
        // Data Type
        Result<RuntimeType<?>> dataType = RuntimeType.getTypeFromKeyword(dataTypeToken.value().toString());
        if (dataType.error() != null) return result.failure(dataType.error());
        else this.dataType = dataType.get();
        
        // Symbol
        symbol = new VariableSymbol(SymbolType.VARIABLE, identifier, this.dataType);
        if (!getSymbolTable().tryAddSymbol(symbol)) return result.failure(new SyntaxException(identifierToken, "Variable '" + identifier + "' already exists!"));
        
        return result.success(null);
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer) { }
    
    @Override
    public void debugPrint(int depth)
    {
        Printing.Debug.print("  ".repeat(depth));
        Printing.Debug.println("ARGUMENT:");
        Printing.Debug.print("  ".repeat(depth + 1));
        Printing.Debug.println("Type: " + dataType.keyword);
        Printing.Debug.print("  ".repeat(depth + 1));
        Printing.Debug.println("Identifier: " + identifier);
    }
    
    @Override
    public Result<Void> interpret(Interpreter interpreter)
    {
        return Result.of(null);
    }
}
