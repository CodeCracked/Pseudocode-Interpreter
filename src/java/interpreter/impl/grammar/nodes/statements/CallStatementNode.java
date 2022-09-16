package interpreter.impl.grammar.nodes.statements;

import interpreter.core.Interpreter;
import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.utils.IO;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.components.ArgumentListNode;
import interpreter.impl.runtime.ModuleSymbol;
import interpreter.impl.runtime.SymbolType;

import java.util.function.BiConsumer;

public class CallStatementNode extends AbstractNode
{
    private final Token identifier;
    private final ArgumentListNode arguments;
    
    private ModuleSymbol module;
    
    public CallStatementNode(Token keyword, Token identifier, ArgumentListNode arguments)
    {
        super(keyword.start(), arguments.end());
        this.identifier = identifier;
        this.arguments = arguments;
    }
    
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
        
        // Arguments
        result.register(arguments.populate(interpreter));
        if (result.error() != null) return result;
        
        // Module Symbol
        module = getSymbolTable().getSymbol(SymbolType.MODULE, identifier.value().toString());
        if (module == null) return result.failure(new SyntaxException(identifier, "Module '" + identifier.value().toString() + "' does not exist! Was it spelled correctly with proper capitalization?"));
        
        return result.success(null);
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer)
    {
        parentChildConsumer.accept(this, arguments);
        arguments.walk(parentChildConsumer);
    }
    
    @Override
    public void debugPrint(int depth)
    {
        IO.Debug.print("  ".repeat(depth));
        IO.Debug.println("CALL:");
        
        IO.Debug.print("  ".repeat(depth + 1));
        IO.Debug.println("Identifier: " + identifier.value().toString());
        
        IO.Debug.print("  ".repeat(depth + 1));
        IO.Debug.println("Arguments:");
        arguments.debugPrint(depth + 2);
    }
    
    @Override
    public Result<Void> interpret(Interpreter interpreter)
    {
        return module.call(interpreter, arguments.arguments, arguments.start(), arguments.end());
    }
}
