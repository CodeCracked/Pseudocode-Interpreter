package interpreter.impl.grammar.nodes.blocks;

import interpreter.core.Interpreter;
import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.runtime.SymbolTable;
import interpreter.core.utils.IO;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.components.ParameterListNode;
import interpreter.impl.runtime.ModuleSymbol;

import java.util.function.BiConsumer;

public class ModuleDefinitionNode extends AbstractNode
{
    private final Token identifier;
    public final ParameterListNode parameters;
    public final BlockNode body;
    
    private SymbolTable bodySymbolTable;
    private ModuleSymbol symbol;
    
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
        return this.bodySymbolTable;
    }
    
    public Result<Void> registerSymbol()
    {
        this.symbol = new ModuleSymbol(identifier.value().toString(), this);
        if (!parent.getSymbolTable().tryAddSymbol(this.symbol)) return Result.fail(new SyntaxException(identifier, "Module '" + identifier.value().toString() + "' already exists!"));
        else return Result.of(null);
    }
    
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
        
        // Create Child Symbol Table
        this.bodySymbolTable = parent.getSymbolTable().createChild();
    
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
        IO.Debug.print("  ".repeat(depth));
        IO.Debug.println("MODULE:");
    
        IO.Debug.print("  ".repeat(depth + 1));
        IO.Debug.println("Identifier: " + identifier.value());
    
        IO.Debug.print("  ".repeat(depth + 1));
        IO.Debug.println("Parameters:");
        parameters.debugPrint(depth + 2);
    
        IO.Debug.print("  ".repeat(depth + 1));
        IO.Debug.println("Body:");
        body.debugPrint(depth + 2);
    }
    
    @Override
    public Result<Void> interpret(Interpreter interpreter)
    {
        return Result.of(null);
    }
}
