package interpreter.impl.grammar.nodes.expressions;

import interpreter.core.Interpreter;
import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.runtime.RuntimeType;
import interpreter.core.utils.Printing;
import interpreter.core.utils.Result;
import interpreter.impl.runtime.RuntimeTypes;
import interpreter.impl.tokens.TokenType;

import java.util.function.BiConsumer;

public class UnaryOpNode extends AbstractValuedNode
{
    private final Token operation;
    private final AbstractValuedNode argument;
    
    private RuntimeType<?> argumentType;
    private RuntimeType<?> runtimeType;
    
    public UnaryOpNode(Token operationToken, AbstractValuedNode argument)
    {
        super(operationToken.start(), argument.end());
        this.operation = operationToken;
        this.argument = argument;
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer)
    {
        parentChildConsumer.accept(this, argument);
        argument.walk(parentChildConsumer);
    }
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
        
        // Populate Argument
        result.register(argument.populate(interpreter));
        if (result.error() != null) return result;
        
        // Argument Type
        Result<RuntimeType<?>> argumentType = argument.getRuntimeType();
        if (argumentType.error() != null) return Result.fail(argumentType.error());
        else this.argumentType = argumentType.get();
        
        // Validate Argument Data Type With Operation
        if (operation.type() == TokenType.MINUS)
        {
            if (argumentType.get() != RuntimeTypes.INTEGER && argumentType.get() != RuntimeTypes.REAL) return result.failure(new SyntaxException(argument, "Expected Integer or Real, found " + argumentType.get().keyword + "!"));
            else this.runtimeType = argumentType.get();
        }
        else return result.failure(new SyntaxException(operation, "Expected '-'!"));
        
        return result.success(null);
    }
    @Override
    public void debugPrint(int depth)
    {
        Printing.Debug.print("  ".repeat(depth));
        Printing.Debug.println(operation.type().name() + ":");
        argument.debugPrint(depth + 1);
    }
    
    @Override
    public Result<RuntimeType<?>> getRuntimeType()
    {
        return Result.of(runtimeType);
    }
    @Override
    public Result<Object> getValue(Interpreter interpreter)
    {
        if (operation.type() == TokenType.MINUS)
        {
            Result<Object> argumentValue = argument.getValue(interpreter);
            if (argumentValue.error() != null) return Result.fail(argumentValue.error());
            
            if (argumentType.equals(RuntimeTypes.INTEGER)) return Result.of(-((Long)argumentValue.get()));
            else if (argumentType.equals(RuntimeTypes.REAL)) return Result.of(-((Double)argumentValue.get()));
            else return Result.fail(new SyntaxException(argument, "Expected Integer or Real, found " + argumentType.keyword + "!"));
        }
        else return Result.fail(new SyntaxException(this, "Unknown unary operator '" + operation.type().name() + "'!"));
    }
}
