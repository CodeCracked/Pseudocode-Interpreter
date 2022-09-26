package interpreter.impl.grammar.nodes;

import interpreter.core.Interpreter;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.runtime.SymbolTable;
import interpreter.core.runtime.VariableSymbol;
import interpreter.core.utils.IO;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.statements.ModuleDefinitionNode;
import interpreter.impl.runtime.ModuleSymbol;
import interpreter.impl.runtime.RuntimeTypes;
import interpreter.impl.runtime.SymbolType;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class ProgramNode extends AbstractNode
{
    private final List<ModuleDefinitionNode> modules;
    private SymbolTable rootSymbolTable;
    
    public ProgramNode(List<ModuleDefinitionNode> modules)
    {
        super(modules.get(0).start(), modules.get(modules.size() - 1).end());
        this.modules = Collections.unmodifiableList(modules);
    }
    
    @Override
    public void createSymbolTable()
    {
        this.rootSymbolTable = new SymbolTable();
        this.rootSymbolTable.tryAddSymbol(new VariableSymbol(SymbolType.VARIABLE, "True", RuntimeTypes.BOOLEAN, true, true));
        this.rootSymbolTable.tryAddSymbol(new VariableSymbol(SymbolType.VARIABLE, "False", RuntimeTypes.BOOLEAN, false, true));
    }
    @Override
    public SymbolTable getSymbolTable()
    {
        return this.rootSymbolTable;
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer)
    {
        for (ModuleDefinitionNode module : modules)
        {
            parentChildConsumer.accept(this, module);
            module.walk(parentChildConsumer);
        }
    }
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
        
        // Register Module Symbol
        for (ModuleDefinitionNode module : modules)
        {
            result.register(module.registerSymbol());
            if (result.error() != null) return result;
        }
        
        // Populate Modules
        for (ModuleDefinitionNode module : modules)
        {
            result.register(module.populate(interpreter));
            if (result.error() != null) return result;
        }
        
        return result.success(null);
    }
    @Override
    public void debugPrint(int depth)
    {
        IO.Debug.print("  ".repeat(depth));
        IO.Debug.println("PROGRAM");
        for (ModuleDefinitionNode module : modules) module.debugPrint(depth + 1);
    }
    @Override
    public Result<Void> interpret(Interpreter interpreter)
    {
        ModuleSymbol mainModule = this.rootSymbolTable.getSymbol(SymbolType.MODULE, "main");
        if (mainModule == null) mainModule = this.rootSymbolTable.getSymbol(SymbolType.MODULE, "Main");
        
        if (mainModule == null) return Result.fail(new IllegalStateException("Program does not have a main module! Was it spelled and capitalized correctly?"));
        else return mainModule.call(interpreter, Collections.emptyList(), null, null);
    }
}
