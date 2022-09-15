package interpreter.impl.grammar.nodes.blocks;

import interpreter.core.Interpreter;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.runtime.Symbol;
import interpreter.core.runtime.SymbolTable;
import interpreter.core.utils.Printing;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.components.ParameterListNode;

import java.util.function.BiConsumer;

public class ModuleDefinitionNode extends AbstractNode
{
    private final Token identifier;
    private final ParameterListNode parameters;
    private final BlockNode body;
    
    private SymbolTable symbolTable;
    
    public ModuleDefinitionNode(Token openKeyword, Token identifier, ParameterListNode parameters, BlockNode body, Token closeKeyword)
    {
        super(openKeyword.start(), closeKeyword.end());
        this.identifier = identifier;
        this.parameters = parameters;
        this.body = body;
    }
    
    @Override
    public SymbolTable getSymbolTable()
    {
        return this.symbolTable;
    }
    
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
        
        // Create Child Symbol Table
        this.symbolTable = parent.getSymbolTable().createChild();
        
        // Parameters
        result.register(parameters.populate(interpreter));
        if (result.error() != null) return result;
        
        // Body
        result.register(body.populate(interpreter));
        if (result.error() != null) return result;
        
        return result.success(null);
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer)
    {
        parentChildConsumer.accept(this, parameters);
        parameters.walk(parentChildConsumer);
        
        parentChildConsumer.accept(this, body);
        body.walk(parentChildConsumer);
    }
    
    @Override
    public void debugPrint(int depth)
    {
        Printing.Debug.print("  ".repeat(depth));
        Printing.Debug.println("MODULE:");
    
        Printing.Debug.print("  ".repeat(depth + 1));
        Printing.Debug.println("Identifier: " + identifier.value());
    
        Printing.Debug.print("  ".repeat(depth + 1));
        Printing.Debug.println("Parameters:");
        parameters.debugPrint(depth + 2);
    
        Printing.Debug.print("  ".repeat(depth + 1));
        Printing.Debug.println("Body:");
        body.debugPrint(depth + 2);
    }
    
    @Override
    public Result<Void> interpret(Interpreter interpreter)
    {
        return Result.of(null);
    }
}
