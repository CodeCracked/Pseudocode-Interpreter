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
    private final Enum<?> operation;
    private final AbstractValuedNode argument;
    private final RuntimeType<?> argumentType;
    private final RuntimeType<?> runtimeType;
    
    private UnaryOpNode(Token operation, AbstractValuedNode argument, RuntimeType<?> argumentType, RuntimeType<?> runtimeType)
    {
        super(operation.start(), argument.end());
        this.operation = operation.type();
        this.argument = argument;
        this.argumentType = argumentType;
        this.runtimeType = runtimeType;
    }
    public static Result<UnaryOpNode> create(Token operationToken, AbstractValuedNode argument)
    {
        Result<RuntimeType<?>> argumentType = argument.getRuntimeType();
        if (argumentType.error() != null) return Result.fail(argumentType.error());
    
        // Validate Argument Data Type
        Enum<?> operation = operationToken.type();
        RuntimeType<?> type;
        if (operation == TokenType.MINUS)
        {
            if (argumentType.get() != RuntimeTypes.INTEGER && argumentType.get() != RuntimeTypes.REAL) return Result.fail(new SyntaxException(argument, "Expected Integer or Real, found " + argumentType.get().keyword + "!"));
            else type = argumentType.get();
        }
        else return Result.fail(new SyntaxException(operationToken, "Expected '-'!"));
        
        return Result.of(new UnaryOpNode(operationToken, argument, argumentType.get(), type));
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer)
    {
        parentChildConsumer.accept(this, argument);
        argument.walk(parentChildConsumer);
    }
    @Override
    public void debugPrint(int depth)
    {
        Printing.Debug.print("  ".repeat(depth));
        Printing.Debug.println(operation.name() + ":");
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
        if (operation == TokenType.MINUS)
        {
            Result<Object> argumentValue = argument.getValue(interpreter);
            if (argumentValue.error() != null) return Result.fail(argumentValue.error());
            
            if (argumentType.equals(RuntimeTypes.INTEGER)) return Result.of(-((Long)argumentValue.get()));
            else if (argumentType.equals(RuntimeTypes.REAL)) return Result.of(-((Double)argumentValue.get()));
            else return Result.fail(new SyntaxException(argument, "Expected Integer or Real, found " + argumentType.keyword + "!"));
        }
        else return Result.fail(new SyntaxException(this, "Unknown unary operator '" + operation.name() + "'!"));
    }
}
