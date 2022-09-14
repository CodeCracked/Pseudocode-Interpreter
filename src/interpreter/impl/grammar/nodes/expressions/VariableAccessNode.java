package interpreter.impl.grammar.nodes.expressions;

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

public class VariableAccessNode extends AbstractValuedNode
{
    private final String identifier;
    
    public VariableAccessNode(Token identifier)
    {
        super(identifier.start(), identifier.end());
        this.identifier = (String)identifier.value();
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer) { }
    
    @Override
    public void debugPrint(int depth)
    {
        Printing.Debug.print("  ".repeat(depth));
        Printing.Debug.println("Access: " + identifier);
    }
    
    @Override
    public void interpret(Interpreter interpreter) { }
    
    @Override
    public RuntimeType<?> getRuntimeType(Interpreter interpreter)
    {
        Symbol symbol = getSymbolTable().getSymbol(SymbolType.VARIABLE, identifier);
        if (symbol != null) return ((VariableSymbol)symbol).getRuntimeType();
        else return null;
    }
    
    @Override
    public Optional<?> getValue(Interpreter interpreter)
    {
        Symbol symbol = getSymbolTable().getSymbol(SymbolType.VARIABLE, identifier);
        if (symbol != null) return Optional.of(((VariableSymbol)symbol).getValue());
        else return Optional.empty();
    }
}
