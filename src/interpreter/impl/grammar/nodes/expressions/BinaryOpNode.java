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

import java.util.Set;
import java.util.function.BiConsumer;

public class BinaryOpNode extends AbstractValuedNode
{
    private static final Set<Enum<?>> mathOperators = Set.of(TokenType.PLUS, TokenType.MINUS, TokenType.MUL, TokenType.DIV, TokenType.MOD);
    private static final Set<RuntimeType<?>> mathTypes = Set.of(RuntimeTypes.INTEGER, RuntimeTypes.REAL);
    
    private final AbstractValuedNode left;
    private final Enum<?> operation;
    private final AbstractValuedNode right;
    
    private final RuntimeType<?> leftType;
    private final RuntimeType<?> rightType;
    private final RuntimeType<?> runtimeType;
    
    private BinaryOpNode(AbstractValuedNode left, Enum<?> operation, AbstractValuedNode right, RuntimeType<?> leftType, RuntimeType<?> rightType, RuntimeType<?> runtimeType)
    {
        super(left.start(), right.end());
        this.left = left;
        this.operation = operation;
        this.right = right;
        
        this.leftType = leftType;
        this.runtimeType = runtimeType;
        this.rightType = rightType;
    }
    public static Result<BinaryOpNode> create(AbstractValuedNode left, Token operationToken, AbstractValuedNode right)
    {
        // Left Argument Type
        Result<RuntimeType<?>> leftType = left.getRuntimeType();
        if (leftType.error() != null) return Result.fail(leftType.error());
        
        // Right Argument Type
        Result<RuntimeType<?>> rightType = right.getRuntimeType();
        if (rightType.error() != null) return Result.fail(rightType.error());
        
        // Validate Argument Data Types Match Operator
        Enum<?> operation = operationToken.type();
        RuntimeType<?> returnType;
        if (mathOperators.contains(operation))
        {
            if (!mathTypes.contains(leftType.get())) return Result.fail(new SyntaxException(left, "Expected Integer or Real, found " + leftType.get().keyword));
            if (!mathTypes.contains(rightType.get())) return Result.fail(new SyntaxException(right, "Expected Integer or Real, found " + rightType.get().keyword));
            
            returnType = leftType.get();
            if (returnType.equals(RuntimeTypes.INTEGER) && rightType.get().equals(RuntimeTypes.REAL)) returnType = RuntimeTypes.REAL;
        }
        else return Result.fail(new SyntaxException(operationToken, "Expected '+', '-', '*', '/', or 'MOD'!"));
        
        return Result.of(new BinaryOpNode(left, operation, right, leftType.get(), rightType.get(), returnType));
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer)
    {
        parentChildConsumer.accept(this, left);
        left.walk(parentChildConsumer);
    }
    @Override
    public void debugPrint(int depth)
    {
        Printing.Debug.print("  ".repeat(depth));
        Printing.Debug.println(operation.name() + ":");
        left.debugPrint(depth + 1);
        right.debugPrint(depth + 1);
    }
    
    @Override
    public Result<RuntimeType<?>> getRuntimeType()
    {
        return Result.of(runtimeType);
    }
    @Override
    public Result<Object> getValue(Interpreter interpreter)
    {
        Result<Object> leftValue = left.getValue(interpreter);
        if (leftValue.error() != null) return Result.fail(leftValue.error());
        Result<?> leftCasted = runtimeType.tryCast(leftValue.get());
        if (leftCasted.error() != null) return Result.fail(leftCasted.error());
    
        Result<Object> rightValue = right.getValue(interpreter);
        if (rightValue.error() != null) return Result.fail(rightValue.error());
        Result<?> rightCasted = runtimeType.tryCast(rightValue.get());
        if (rightCasted.error() != null) return Result.fail(rightCasted.error());
        
        if (operation == TokenType.PLUS)
        {
            if (runtimeType.equals(RuntimeTypes.INTEGER)) return Result.of(((Long)leftCasted.get()) + ((Long)rightCasted.get()));
            else if (runtimeType.equals(RuntimeTypes.REAL)) return Result.of(((Double)leftCasted.get()) + ((Double)rightCasted.get()));
            else return Result.fail(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else if (operation == TokenType.MINUS)
        {
            if (runtimeType.equals(RuntimeTypes.INTEGER)) return Result.of(((Long)leftCasted.get()) - ((Long)rightCasted.get()));
            else if (runtimeType.equals(RuntimeTypes.REAL)) return Result.of(((Double)leftCasted.get()) - ((Double)rightCasted.get()));
            else return Result.fail(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else if (operation == TokenType.MUL)
        {
            if (runtimeType.equals(RuntimeTypes.INTEGER)) return Result.of(((Long)leftCasted.get()) * ((Long)rightCasted.get()));
            else if (runtimeType.equals(RuntimeTypes.REAL)) return Result.of(((Double)leftCasted.get()) * ((Double)rightCasted.get()));
            else return Result.fail(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else if (operation == TokenType.DIV)
        {
            if (runtimeType.equals(RuntimeTypes.INTEGER)) return Result.of(((Long)leftCasted.get()) / ((Long)rightCasted.get()));
            else if (runtimeType.equals(RuntimeTypes.REAL)) return Result.of(((Double)leftCasted.get()) / ((Double)rightCasted.get()));
            else return Result.fail(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else if (operation == TokenType.MOD)
        {
            if (runtimeType.equals(RuntimeTypes.INTEGER)) return Result.of(((Long)leftCasted.get()) % ((Long)rightCasted.get()));
            else if (runtimeType.equals(RuntimeTypes.REAL)) return Result.of(((Double)leftCasted.get()) % ((Double)rightCasted.get()));
            else return Result.fail(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else return Result.fail(new SyntaxException(this, "Unknown operator '" + operation.name() + "'!"));
    }
}
