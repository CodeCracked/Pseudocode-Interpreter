package interpreter.impl.grammar.nodes.statements;

import interpreter.core.Interpreter;
import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.runtime.RuntimeType;
import interpreter.core.runtime.Symbol;
import interpreter.core.runtime.VariableSymbol;
import interpreter.core.utils.Printing;
import interpreter.core.utils.Result;
import interpreter.impl.runtime.SymbolType;

import java.util.function.BiConsumer;

public class DeclareStatementNode extends AbstractNode
{
    private final RuntimeType<?> dataType;
    private final String identifier;
    private final AbstractValuedNode initialValue;
    
    private DeclareStatementNode(Token keyword, RuntimeType<?> dataType, Token identifier, AbstractValuedNode initialValue)
    {
        super(keyword.start(), initialValue != null ? initialValue.end() : identifier.end());
    
        this.dataType = dataType;
        this.identifier = (String)identifier.value();
        this.initialValue = initialValue;
    }
    
    public static Result<DeclareStatementNode> create(Token keyword, Token dataType, Token identifier, AbstractValuedNode initialValue)
    {
        Result<RuntimeType<?>> type = RuntimeType.getTypeFromKeyword(dataType.value().toString());
        if (type.error() != null) return Result.fail(type.error());
        else return Result.of(new DeclareStatementNode(keyword, type.get(), identifier, initialValue));
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
    public void debugPrint(int depth)
    {
        Printing.Debug.print("  ".repeat(depth));
        Printing.Debug.println("Declare " + dataType.keyword);
        
        Printing.Debug.print("  ".repeat(depth + 1));
        Printing.Debug.println("Identifier: " + identifier);
        
        Printing.Debug.print("  ".repeat(depth + 1));
        Printing.Debug.print("Initial Value: ");
        if (initialValue != null)
        {
            Printing.Debug.println();
            initialValue.debugPrint(depth + 2);
        }
        else Printing.Debug.println("None");
    }
    @Override
    public Result<Void> interpret(Interpreter interpreter)
    {
        VariableSymbol variable = new VariableSymbol(SymbolType.VARIABLE, identifier, dataType);
        if (initialValue != null)
        {
            Result<Object> expressionResult = initialValue.getValue(interpreter);
            if (expressionResult.error() != null) return Result.fail(expressionResult.error());
            
            Result<?> assignResult = variable.setValue(expressionResult.get());
            if (assignResult.error() != null) return Result.fail(assignResult.error());
        }
        
        if (getSymbolTable().tryAddSymbol(variable)) return Result.of(null);
        else return Result.fail(new SyntaxException(this, "Variable '" + identifier + "' already exists! Did you mean to Set it instead of Declare it?"));
    }
}
