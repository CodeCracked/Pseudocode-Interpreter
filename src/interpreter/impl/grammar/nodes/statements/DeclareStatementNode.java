package interpreter.impl.grammar.nodes.statements;

import interpreter.core.Interpreter;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.runtime.RuntimeType;
import interpreter.core.runtime.Symbol;
import interpreter.core.runtime.VariableSymbol;
import interpreter.core.utils.Printing;
import interpreter.impl.runtime.SymbolType;

import java.util.Optional;
import java.util.function.BiConsumer;

public class DeclareStatementNode extends AbstractNode
{
    private final RuntimeType<?> dataType;
    private final String identifier;
    private final AbstractValuedNode initialValue;
    
    public DeclareStatementNode(Token keyword, Token dataType, Token identifier, AbstractValuedNode initialValue)
    {
        super(keyword.start(), initialValue != null ? initialValue.end() : identifier.end());
    
        Optional<RuntimeType<?>> type = RuntimeType.getTypeFromKeyword(dataType.value().toString());
        this.dataType = type.orElse(null);
        this.identifier = (String)identifier.value();
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
    public void interpret(Interpreter interpreter)
    {
        Symbol variable = new VariableSymbol(SymbolType.VARIABLE, identifier, dataType, initialValue != null ? initialValue.getValue(interpreter) : Optional.empty());
        getSymbolTable().tryAddSymbol(variable);
    }
}
