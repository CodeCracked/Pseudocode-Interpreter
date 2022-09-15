package interpreter.impl.runtime;

import interpreter.core.Interpreter;
import interpreter.core.runtime.Symbol;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.blocks.ModuleDefinitionNode;
import interpreter.impl.grammar.nodes.components.ValueSetNode;

public class ModuleSymbol extends Symbol
{
    public final ModuleDefinitionNode moduleDefinition;
    
    public ModuleSymbol(String name, ModuleDefinitionNode moduleDefinition)
    {
        super(SymbolType.MODULE, name);
        this.moduleDefinition = moduleDefinition;
    }
    
    public Result<Void> call(Interpreter interpreter, ValueSetNode arguments)
    {
        // TODO: Validate and pass arguments
        return moduleDefinition.body.interpret(interpreter);
    }
    
    @Override
    public Symbol clone()
    {
        return new ModuleSymbol(name, moduleDefinition);
    }
}
