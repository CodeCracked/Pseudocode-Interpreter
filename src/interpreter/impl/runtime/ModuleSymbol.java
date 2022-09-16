package interpreter.impl.runtime;

import interpreter.core.Interpreter;
import interpreter.core.exceptions.SyntaxException;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.runtime.RuntimeType;
import interpreter.core.runtime.Symbol;
import interpreter.core.runtime.VariableSymbol;
import interpreter.core.source.SourcePosition;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.blocks.ModuleDefinitionNode;

import java.util.List;

public class ModuleSymbol extends Symbol
{
    public final ModuleDefinitionNode moduleDefinition;
    
    public ModuleSymbol(String name, ModuleDefinitionNode moduleDefinition)
    {
        super(SymbolType.MODULE, name);
        this.moduleDefinition = moduleDefinition;
    }
    
    public Result<Void> call(Interpreter interpreter, List<AbstractValuedNode> arguments, SourcePosition start, SourcePosition end)
    {
        Result<Void> result = new Result<>();
        
        // Validate Argument Count
        int argumentCount = arguments.size();
        int expectedArgumentCount = moduleDefinition.parameters.parameters.size();
        if (argumentCount != expectedArgumentCount) return result.failure(new SyntaxException(start, end, "Expected " + expectedArgumentCount + " parameters, found " + argumentCount + "!"));
        
        // Pass Arguments
        for (int i = 0; i < argumentCount; i++)
        {
            VariableSymbol parameter = moduleDefinition.parameters.parameters.get(i).getVariableSymbol();
            AbstractValuedNode argument = arguments.get(i);
            
            // Get Argument Value
            Result<Object> argumentValue = argument.getValue(interpreter);
            if (argumentValue.error() != null) return result.failure(argumentValue.error());
            
            // Pass Argument to Parameter
            Result<?> passResult = parameter.setValue(argumentValue.get());
            if (passResult.error() != null)
            {
                RuntimeType<?> expectedType = parameter.getRuntimeType();
                Result<RuntimeType<?>> foundType = argument.getRuntimeType();
                
                if (foundType.error() != null) return result.failure(new SyntaxException(argument, "Expected " + expectedType.keyword + ", found Unknown!"));
                else return result.failure(new SyntaxException(argument, "Expected " + expectedType.keyword + ", found " + foundType.get().keyword + "!"));
            }
        }
        
        return moduleDefinition.body.interpret(interpreter);
    }
    
    @Override
    public Symbol clone()
    {
        return new ModuleSymbol(name, moduleDefinition);
    }
}
