package interpreter.impl.runtime;

import interpreter.core.Interpreter;
import interpreter.core.exceptions.SyntaxException;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.runtime.RuntimeType;
import interpreter.core.runtime.Symbol;
import interpreter.core.runtime.VariableSymbol;
import interpreter.core.source.SourcePosition;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.statements.ModuleDefinitionNode;
import interpreter.impl.grammar.nodes.components.ParameterNode;
import interpreter.impl.grammar.nodes.expressions.VariableAccessNode;

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
            ParameterNode parameterDefinition = moduleDefinition.parameters.parameters.get(i);
            VariableSymbol parameter = parameterDefinition.getVariableSymbol();
            AbstractValuedNode argument = arguments.get(i);
            
            // Check For Reference Compliance
            if (parameterDefinition.passByReference)
            {
                if (!(argument instanceof VariableAccessNode variableAccessNode)) return result.failure(new SyntaxException(argument, "Only variable identifiers can be passed to Ref parameters!"));
                else if (variableAccessNode.getVariableSymbol().isConstant()) return result.failure(new SyntaxException(argument, "Cannot pass a constant variable by reference!"));
            }
            
            // Get Argument Value
            Result<Object> argumentValue = result.registerIssues(argument.getValue(interpreter));
            if (result.error() != null) return result;
            
            // Pass Argument to Parameter
            result.registerIssues(parameter.setValue(argumentValue.get(), argument));
            if (result.error() != null)
            {
                RuntimeType<?> expectedType = parameter.getRuntimeType();
                Result<RuntimeType<?>> foundType = result.registerIssues(argument.getRuntimeType());
    
                if (foundType.error() != null) return result.failure(new SyntaxException(argument, "Expected " + expectedType.keyword + ", found Unknown!"));
                else return result.failure(new SyntaxException(argument, "Expected " + expectedType.keyword + ", found " + foundType.get().keyword + "!"));
            }
        }
        
        // Run Module
        result.register(moduleDefinition.body.interpret(interpreter));
        if (result.error() != null) return result;
        
        // Pass Back Reference Parameters
        for (int i = 0; i < argumentCount; i++)
        {
            // Check if parameter is a reference
            ParameterNode parameter = moduleDefinition.parameters.parameters.get(i);
            if (!parameter.passByReference) continue;
            
            // Pass Back New Value
            AbstractValuedNode argument = arguments.get(i);
            if (argument instanceof VariableAccessNode targetVariable)
            {
                Result<Object> parameterValue = result.registerIssues(parameter.getVariableSymbol().getValue(argument));
                if (result.error() != null) return result;
                
                result.registerIssues(targetVariable.getVariableSymbol().setValue(parameterValue.get(), parameter));
                if (result.error() != null) return result;
            }
            else return result.failure(new SyntaxException(argument, "Only variable identifiers can be passed to Ref parameters!"));
        }
        
        return result.success(null);
    }
}
