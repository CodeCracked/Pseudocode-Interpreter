package interpreter.impl.grammar.nodes;

import interpreter.core.Interpreter;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.runtime.SymbolTable;
import interpreter.core.utils.Printing;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.blocks.ModuleDefinitionNode;

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
        Printing.Debug.print("  ".repeat(depth));
        Printing.Debug.println("PROGRAM");
        for (ModuleDefinitionNode module : modules) module.debugPrint(depth + 1);
    }
    @Override
    public Result<Void> interpret(Interpreter interpreter)
    {
        // TODO: Run main module
        return Result.of(null);
        //return block.interpret(interpreter);
    }
}
