package interpreter.impl.grammar.nodes.statements;

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

public class DeclareStatementNode extends AbstractNode
{
    private final Token dataTypeToken;
    private final Token identifierToken;
    private final String identifier;
    private final AbstractValuedNode initialValue;
    
    private RuntimeType<?> dataType;
    private VariableSymbol symbol;
    
    public DeclareStatementNode(Token keyword, Token dataType, Token identifier, AbstractValuedNode initialValue)
    {
        super(keyword.start(), initialValue != null ? initialValue.end() : identifier.end());
    
        this.dataTypeToken = dataType;
        this.identifierToken = identifier;
        this.identifier = identifier.value().toString();
        this.initialValue = initialValue;
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer)
    {
        if (initialValue != null)
        {
            parentChildConsumer.accept(this, initialValue);
            initialValue.walk(parentChildConsumer);
        }
    }
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
        
        // Data Type
        Result<RuntimeType<?>> dataType = result.registerIssues(RuntimeType.getTypeFromKeyword(this.dataTypeToken.value().toString()));
        if (result.error() != null) return result;
        this.dataType = dataType.get();
        
        // Initial Value
        if (initialValue != null)
        {
            result.register(initialValue.populate(interpreter));
            if (result.error() != null) return result;
        }
        
        // Symbol
        if (identifier.charAt(0) != identifier.toLowerCase().charAt(0)) result.warn(new SyntaxException(identifierToken, "Variable identifiers should be camelCase!"));
        symbol = new VariableSymbol(SymbolType.VARIABLE, identifier, this.dataType, false);
        if (getSymbolTable().tryAddSymbol(symbol)) return result.success(null);
        else return result.failure(new SyntaxException(identifierToken, "Variable '" + identifier + "' already exists! Did you mean to Set it instead of Declare it?"));
    }
    @Override
    public void debugPrint(int depth)
    {
        IO.Debug.print("  ".repeat(depth));
        IO.Debug.println("DECLARE " + dataType.keyword + ":");
        
        IO.Debug.print("  ".repeat(depth + 1));
        IO.Debug.println("Identifier: " + identifier);
        
        IO.Debug.print("  ".repeat(depth + 1));
        IO.Debug.print("Initial Value: ");
        if (initialValue != null)
        {
            IO.Debug.println();
            initialValue.debugPrint(depth + 2);
        }
        else IO.Debug.println("None");
    }
    @Override
    public Result<Void> interpret(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();

        if (initialValue != null)
        {
            Result<Object> expressionResult = result.registerIssues(initialValue.getValue(interpreter));
            if (result.error() != null) return result;
        
            result.registerIssues(symbol.setValue(expressionResult.get(), this));
            if (result.error() != null) return result;
        }
        
        return Result.of(null);
    }
}
