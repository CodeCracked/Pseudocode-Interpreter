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
        Result<RuntimeType<?>> leftType = result.registerIssues(left.getRuntimeType());
        if (result.error() != null) return result;
        this.leftCastType = leftType.get();
    
        // Right Argument Type
        Result<RuntimeType<?>> rightType = result.registerIssues(right.getRuntimeType());
        if (result.error() != null) return result;
        this.rightCastType = rightType.get();
    
        // Validate Argument Data Types Match Operator
        if (mathOperators.contains(operation.type()))
        {
            if (!mathTypes.contains(leftType.get())) return result.failure(new SyntaxException(left, "Expected Integer or Real, found " + leftType.get().keyword));
            if (!mathTypes.contains(rightType.get())) return result.failure(new SyntaxException(right, "Expected Integer or Real, found " + rightType.get().keyword));
        
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
                if (!mathTypes.contains(leftType.get())) return result.failure(new SyntaxException(left, "Expected Integer or Real, found " + leftType.get().keyword));
                if (!mathTypes.contains(rightType.get())) return result.failure(new SyntaxException(right, "Expected Integer or Real, found " + rightType.get().keyword));
                
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
            if (!leftType.get().equals(RuntimeTypes.BOOLEAN)) return result.failure(new SyntaxException(left, "Expected Boolean, found " + leftType.get().keyword));
            if (!rightType.get().equals(RuntimeTypes.BOOLEAN)) return result.failure(new SyntaxException(right, "Expected Boolean, found " + rightType.get().keyword));
            runtimeType = RuntimeTypes.BOOLEAN;
        }
        else return result.failure(new SyntaxException(operation, "Expected operator!"));
        
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
        Result<Object> result = new Result<>();

        Result<Object> leftValue = result.registerIssues(left.getValue(interpreter));
        if (result.error() != null) return result;
        Result<?> leftCasted = result.registerIssues(leftCastType.tryCast(leftValue.get()));
        if (result.error() != null) return result;
    
        Result<Object> rightValue = result.registerIssues(right.getValue(interpreter));
        if (result.error() != null) return result;
        Result<?> rightCasted = result.registerIssues(rightCastType.tryCast(rightValue.get()));
        if (result.error() != null) return result;
        
        //region Arithmetic Operators
        if (operation.type() == TokenType.PLUS)
        {
            if (runtimeType.equals(RuntimeTypes.INTEGER)) return result.success(((Long)leftCasted.get()) + ((Long)rightCasted.get()));
            else if (runtimeType.equals(RuntimeTypes.REAL)) return result.success(((Double)leftCasted.get()) + ((Double)rightCasted.get()));
            else return result.failure(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else if (operation.type() == TokenType.MINUS)
        {
            if (runtimeType.equals(RuntimeTypes.INTEGER)) return result.success(((Long)leftCasted.get()) - ((Long)rightCasted.get()));
            else if (runtimeType.equals(RuntimeTypes.REAL)) return result.success(((Double)leftCasted.get()) - ((Double)rightCasted.get()));
            else return result.failure(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else if (operation.type() == TokenType.MUL)
        {
            if (runtimeType.equals(RuntimeTypes.INTEGER)) return result.success(((Long)leftCasted.get()) * ((Long)rightCasted.get()));
            else if (runtimeType.equals(RuntimeTypes.REAL)) return result.success(((Double)leftCasted.get()) * ((Double)rightCasted.get()));
            else return result.failure(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else if (operation.type() == TokenType.DIV)
        {
            if (runtimeType.equals(RuntimeTypes.INTEGER)) return result.success(((Long)leftCasted.get()) / ((Long)rightCasted.get()));
            else if (runtimeType.equals(RuntimeTypes.REAL)) return result.success(((Double)leftCasted.get()) / ((Double)rightCasted.get()));
            else return result.failure(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else if (operation.type() == TokenType.POW)
        {
            if (runtimeType.equals(RuntimeTypes.INTEGER)) return result.success((long)Math.pow((Long)leftCasted.get(), (Long)rightCasted.get()));
            else if (runtimeType.equals(RuntimeTypes.REAL)) return result.success(Math.pow((Double)leftCasted.get(), (Double)rightCasted.get()));
            else return result.failure(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else if (operation.type() == TokenType.MOD)
        {
            if (runtimeType.equals(RuntimeTypes.INTEGER)) return result.success(((Long)leftCasted.get()) % ((Long)rightCasted.get()));
            else if (runtimeType.equals(RuntimeTypes.REAL)) return result.success(((Double)leftCasted.get()) % ((Double)rightCasted.get()));
            else return result.failure(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        //endregion
        //region Comparison Operators
        else if (operation.type() == TokenType.EQUALS) return result.success(leftCasted.get().equals(rightCasted.get()));
        else if (operation.type() == TokenType.NOT_EQUALS) return result.success(!leftCasted.get().equals(rightCasted.get()));
        else if (operation.type() == TokenType.GREATER)
        {
            if (leftCastType.equals(RuntimeTypes.INTEGER)) return result.success(((Long)leftCasted.get()) > ((Long)rightCasted.get()));
            else if (leftCastType.equals(RuntimeTypes.REAL)) return result.success(((Double)leftCasted.get()) > ((Double)rightCasted.get()));
            else return result.failure(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else if (operation.type() == TokenType.LESS)
        {
            if (leftCastType.equals(RuntimeTypes.INTEGER)) return result.success(((Long)leftCasted.get()) < ((Long)rightCasted.get()));
            else if (leftCastType.equals(RuntimeTypes.REAL)) return result.success(((Double)leftCasted.get()) < ((Double)rightCasted.get()));
            else return result.failure(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else if (operation.type() == TokenType.GREATER_EQUAL)
        {
            if (leftCastType.equals(RuntimeTypes.INTEGER)) return result.success(((Long)leftCasted.get()) >= ((Long)rightCasted.get()));
            else if (leftCastType.equals(RuntimeTypes.REAL)) return result.success(((Double)leftCasted.get()) >= ((Double)rightCasted.get()));
            else return result.failure(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        else if (operation.type() == TokenType.LESS_EQUAL)
        {
            if (leftCastType.equals(RuntimeTypes.INTEGER)) return result.success(((Long)leftCasted.get()) <= ((Long)rightCasted.get()));
            else if (leftCastType.equals(RuntimeTypes.REAL)) return result.success(((Double)leftCasted.get()) <= ((Double)rightCasted.get()));
            else return result.failure(new SyntaxException(this, "Expected Integer or Real, found " + runtimeType.keyword + "!"));
        }
        //endregion
        //region Boolean Operators
        else if (operation.type() == TokenType.AND) return result.success((Boolean)leftCasted.get() && (Boolean)rightCasted.get());
        else if (operation.type() == TokenType.OR) return result.success((Boolean)leftCasted.get() || (Boolean)rightCasted.get());
        //endregion
        
        else return result.failure(new SyntaxException(this, "Unknown operator '" + operation.type().name() + "'!"));
    }
}
