package interpreter.impl.grammar.nodes.expressions;

import interpreter.core.Interpreter;
import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.runtime.RuntimeType;
import interpreter.core.utils.IO;
import interpreter.core.utils.Result;
import interpreter.impl.runtime.RuntimeTypes;
import interpreter.impl.tokens.TokenType;

import java.util.Set;
import java.util.function.BiConsumer;

public class BinaryOpNode extends AbstractValuedNode
{
    private static final Set<Enum<?>> mathOperators = Set.of(TokenType.PLUS, TokenType.MINUS, TokenType.MUL, TokenType.DIV, TokenType.POW, TokenType.MOD);
    private static final Set<RuntimeType<?>> mathTypes = Set.of(RuntimeTypes.INTEGER, RuntimeTypes.REAL);
    
    private final AbstractValuedNode left;
    private final Token operation;
    private final AbstractValuedNode right;
    
    private RuntimeType<?> leftType;
    private RuntimeType<?> rightType;
    private RuntimeType<?> runtimeType;
    
    public BinaryOpNode(AbstractValuedNode left, Token operation, AbstractValuedNode right)
    {
        super(left.start(), right.end());
        this.left = left;
        this.operation = operation;
        this.right = right;
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer)
    {
        parentChildConsumer.accept(this, left);
        left.walk(parentChildConsumer);
        
        parentChildConsumer.accept(this, right);
        right.walk(parentChildConsumer);
    }
    
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
        
        // Populate Arguments
        result.register(left.populate(interpreter));
        if (result.error() != null) return result;
        result.register(right.populate(interpreter));
        if (result.error() != null) return result;
        
        // Left Argument Type
        Result<RuntimeType<?>> leftType = left.getRuntimeType();
        if (leftType.error() != null) return result.failure(leftType.error());
        else this.leftType = leftType.get();
    
        // Right Argument Type
        Result<RuntimeType<?>> rightType = right.getRuntimeType();
        if (rightType.error() != null) return result.failure(rightType.error());
        else this.rightType = rightType.get();
    
        // Validate Argument Data Types Match Operator
        if (mathOperators.contains(operation.type()))
        {
            if (!mathTypes.contains(leftType.get())) return Result.fail(new SyntaxException(left, "Expected Integer or Real, found " + leftType.get().keyword));
            if (!mathTypes.contains(rightType.get())) return Result.fail(new SyntaxException(right, "Expected Integer or Real, found " + rightType.get().keyword));
        
            runtimeType = leftType.get();
            if (runtimeType.equals(RuntimeTypes.INTEGER) && rightType.get().equals(RuntimeTypes.REAL)) runtimeType = RuntimeTypes.REAL;
        }
        else return Result.fail(new SyntaxException(operation, "Expected '+', '-', '*', '/', '^', or 'MOD'!"));
        
        return result.success(null);
    }
    
    @Override
    public void debugPrint(int depth)
    {
        IO.Debug.print("  ".repeat(depth));
        IO.Debug.println(operation.type().name() + ":");
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
        
        if (operation.type() == TokenType.PLUS)
        {
            if (runtimeType.equals(RuntimeTypes.INTEGER)) return Result.of(((Long)leftCasted.get()) + ((Long)rightCasted.get()));
            else if (runtimeType.equals(RuntimeTypes.REAL)) return Result.of(((Double)leftCasted.get()) + ((Double)rightCasted.get()));
            else return Result.fail(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else if (operation.type() == TokenType.MINUS)
        {
            if (runtimeType.equals(RuntimeTypes.INTEGER)) return Result.of(((Long)leftCasted.get()) - ((Long)rightCasted.get()));
            else if (runtimeType.equals(RuntimeTypes.REAL)) return Result.of(((Double)leftCasted.get()) - ((Double)rightCasted.get()));
            else return Result.fail(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else if (operation.type() == TokenType.MUL)
        {
            if (runtimeType.equals(RuntimeTypes.INTEGER)) return Result.of(((Long)leftCasted.get()) * ((Long)rightCasted.get()));
            else if (runtimeType.equals(RuntimeTypes.REAL)) return Result.of(((Double)leftCasted.get()) * ((Double)rightCasted.get()));
            else return Result.fail(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else if (operation.type() == TokenType.DIV)
        {
            if (runtimeType.equals(RuntimeTypes.INTEGER)) return Result.of(((Long)leftCasted.get()) / ((Long)rightCasted.get()));
            else if (runtimeType.equals(RuntimeTypes.REAL)) return Result.of(((Double)leftCasted.get()) / ((Double)rightCasted.get()));
            else return Result.fail(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else if (operation.type() == TokenType.POW)
        {
            if (runtimeType.equals(RuntimeTypes.INTEGER)) return Result.of((long)Math.pow((Long)leftCasted.get(), (Long)rightCasted.get()));
            else if (runtimeType.equals(RuntimeTypes.REAL)) return Result.of(Math.pow((Double)leftCasted.get(), (Double)rightCasted.get()));
            else return Result.fail(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else if (operation.type() == TokenType.MOD)
        {
            if (runtimeType.equals(RuntimeTypes.INTEGER)) return Result.of(((Long)leftCasted.get()) % ((Long)rightCasted.get()));
            else if (runtimeType.equals(RuntimeTypes.REAL)) return Result.of(((Double)leftCasted.get()) % ((Double)rightCasted.get()));
            else return Result.fail(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else return Result.fail(new SyntaxException(this, "Unknown operator '" + operation.type().name() + "'!"));
    }
}
