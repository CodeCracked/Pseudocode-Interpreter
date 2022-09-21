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
    private static final Set<Enum<?>> comparisonOperators = Set.of(TokenType.EQUALS, TokenType.NOT_EQUALS, TokenType.GREATER, TokenType.LESS, TokenType.GREATER_EQUAL, TokenType.LESS_EQUAL);
    private static final Set<Enum<?>> booleanOperators = Set.of(TokenType.AND, TokenType.OR);
    private static final Set<RuntimeType<?>> mathTypes = Set.of(RuntimeTypes.INTEGER, RuntimeTypes.REAL);
    
    private final AbstractValuedNode left;
    private final Token operation;
    private final AbstractValuedNode right;
    
    private RuntimeType<?> leftCastType;
    private RuntimeType<?> rightCastType;
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
        else this.leftCastType = leftType.get();
    
        // Right Argument Type
        Result<RuntimeType<?>> rightType = right.getRuntimeType();
        if (rightType.error() != null) return result.failure(rightType.error());
        else this.rightCastType = rightType.get();
    
        // Validate Argument Data Types Match Operator
        if (mathOperators.contains(operation.type()))
        {
            if (!mathTypes.contains(leftType.get())) return Result.fail(new SyntaxException(left, "Expected Integer or Real, found " + leftType.get().keyword));
            if (!mathTypes.contains(rightType.get())) return Result.fail(new SyntaxException(right, "Expected Integer or Real, found " + rightType.get().keyword));
        
            runtimeType = leftType.get();
            if (runtimeType.equals(RuntimeTypes.INTEGER) && rightType.get().equals(RuntimeTypes.REAL)) runtimeType = RuntimeTypes.REAL;
            leftCastType = runtimeType;
            rightCastType = runtimeType;
        }
        else if (comparisonOperators.contains(operation.type()))
        {
            // Non-Equality comparisons
            if (operation.type() != TokenType.EQUALS && operation.type() != TokenType.NOT_EQUALS)
            {
                if (!mathTypes.contains(leftType.get())) return Result.fail(new SyntaxException(left, "Expected Integer or Real, found " + leftType.get().keyword));
                if (!mathTypes.contains(rightType.get())) return Result.fail(new SyntaxException(right, "Expected Integer or Real, found " + rightType.get().keyword));
                
                leftCastType = leftType.get();
                if (leftCastType.equals(RuntimeTypes.INTEGER) && rightType.get().equals(RuntimeTypes.REAL)) leftCastType = RuntimeTypes.REAL;
                rightCastType = leftCastType;
                runtimeType = RuntimeTypes.BOOLEAN;
            }
            // Equality comparisons
            else
            {
                runtimeType = RuntimeTypes.BOOLEAN;
                leftCastType = leftType.get();
                rightCastType = rightType.get();
            }
        }
        else if (booleanOperators.contains(operation.type()))
        {
            if (!leftType.get().equals(RuntimeTypes.BOOLEAN)) return Result.fail(new SyntaxException(left, "Expected Boolean, found " + leftType.get().keyword));
            if (!rightType.get().equals(RuntimeTypes.BOOLEAN)) return Result.fail(new SyntaxException(right, "Expected Boolean, found " + rightType.get().keyword));
            runtimeType = RuntimeTypes.BOOLEAN;
        }
        else return Result.fail(new SyntaxException(operation, "Expected operator!"));
        
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
        Result<?> leftCasted = leftCastType.tryCast(leftValue.get());
        if (leftCasted.error() != null) return Result.fail(leftCasted.error());
    
        Result<Object> rightValue = right.getValue(interpreter);
        if (rightValue.error() != null) return Result.fail(rightValue.error());
        Result<?> rightCasted = rightCastType.tryCast(rightValue.get());
        if (rightCasted.error() != null) return Result.fail(rightCasted.error());
        
        //region Arithmetic Operators
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
        //endregion
        //region Comparison Operators
        else if (operation.type() == TokenType.EQUALS) return Result.of(leftCasted.get().equals(rightCasted.get()));
        else if (operation.type() == TokenType.NOT_EQUALS) return Result.of(!leftCasted.get().equals(rightCasted.get()));
        else if (operation.type() == TokenType.GREATER)
        {
            if (leftCastType.equals(RuntimeTypes.INTEGER)) return Result.of(((Long)leftCasted.get()) > ((Long)rightCasted.get()));
            else if (leftCastType.equals(RuntimeTypes.REAL)) return Result.of(((Double)leftCasted.get()) > ((Double)rightCasted.get()));
            else return Result.fail(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else if (operation.type() == TokenType.LESS)
        {
            if (leftCastType.equals(RuntimeTypes.INTEGER)) return Result.of(((Long)leftCasted.get()) < ((Long)rightCasted.get()));
            else if (leftCastType.equals(RuntimeTypes.REAL)) return Result.of(((Double)leftCasted.get()) < ((Double)rightCasted.get()));
            else return Result.fail(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else if (operation.type() == TokenType.GREATER_EQUAL)
        {
            if (leftCastType.equals(RuntimeTypes.INTEGER)) return Result.of(((Long)leftCasted.get()) >= ((Long)rightCasted.get()));
            else if (leftCastType.equals(RuntimeTypes.REAL)) return Result.of(((Double)leftCasted.get()) >= ((Double)rightCasted.get()));
            else return Result.fail(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else if (operation.type() == TokenType.LESS_EQUAL)
        {
            if (leftCastType.equals(RuntimeTypes.INTEGER)) return Result.of(((Long)leftCasted.get()) <= ((Long)rightCasted.get()));
            else if (leftCastType.equals(RuntimeTypes.REAL)) return Result.of(((Double)leftCasted.get()) <= ((Double)rightCasted.get()));
            else return Result.fail(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        //endregion
        //region Boolean Operators
        else if (operation.type() == TokenType.AND) return Result.of((Boolean)leftCasted.get() && (Boolean)rightCasted.get());
        else if (operation.type() == TokenType.OR) return Result.of((Boolean)leftCasted.get() || (Boolean)rightCasted.get());
        //endregion
        
        else return Result.fail(new SyntaxException(this, "Unknown operator '" + operation.type().name() + "'!"));
    }
}
