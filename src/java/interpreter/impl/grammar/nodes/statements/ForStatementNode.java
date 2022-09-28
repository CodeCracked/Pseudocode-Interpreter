package interpreter.impl.grammar.nodes.statements;

import interpreter.core.Interpreter;
import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.runtime.RuntimeType;
import interpreter.core.runtime.VariableSymbol;
import interpreter.core.utils.IO;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.expressions.LiteralValueNode;
import interpreter.impl.runtime.RuntimeTypes;
import interpreter.impl.runtime.SymbolType;
import interpreter.impl.tokens.TokenType;

import java.util.function.BiConsumer;

public class ForStatementNode extends AbstractNode
{
    private final Token counterIdentifier;
    private final AbstractValuedNode initialValue;
    private final AbstractValuedNode maxValue;
    private final AbstractValuedNode step;
    private final AbstractNode body;
    
    private VariableSymbol counter;
    
    public ForStatementNode(Token forKeyword, Token counterIdentifier, AbstractValuedNode initialValue, AbstractValuedNode maxValue, AbstractValuedNode step, AbstractNode body, Token closeToken)
    {
        super(forKeyword.start(), closeToken.end());
        
        this.counterIdentifier = counterIdentifier;
        this.initialValue = initialValue;
        this.maxValue = maxValue;
        this.step = step != null ? step : new LiteralValueNode(new Token(TokenType.INTEGER_LITERAL, 1, maxValue.start().clone(), maxValue.start()));
        this.body = body;
    }
    
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
    
        // Counter Symbol
        counter = getSymbolTable().getSymbol(SymbolType.VARIABLE, counterIdentifier.value().toString());
        if (counter == null) return result.failure(new SyntaxException(counterIdentifier, "Cannot find variable '" + counterIdentifier.value().toString() + "'! Are you sure it was spelled and capitalized correctly?"));
        else if (counter.isConstant()) return result.failure(new SyntaxException(counterIdentifier, "Cannot use constant variables in For loops!"));
        else
        {
            RuntimeType<?> counterType = counter.getRuntimeType();
            if (!counterType.equals(RuntimeTypes.INTEGER) && !counterType.equals(RuntimeTypes.REAL)) return result.failure(new SyntaxException(counterIdentifier, "Expected Integer or Real variable, found " + counterType.keyword + "!"));
        }
    
        // Max Value
        result.register(initialValue.populate(interpreter));
        if (result.error() != null) return result;
        
        // Max Value
        result.register(maxValue.populate(interpreter));
        if (result.error() != null) return result;
    
        // Step
        result.register(step.populate(interpreter));
        if (result.error() != null) return result;
        
        // Body
        result.register(body.populate(interpreter));
        if (result.error() != null) return result;
        
        return result.success(null);
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer)
    {
        parentChildConsumer.accept(this, initialValue);
        initialValue.walk(parentChildConsumer);
        
        parentChildConsumer.accept(this, maxValue);
        maxValue.walk(parentChildConsumer);
    
        parentChildConsumer.accept(this, step);
        body.walk(parentChildConsumer);
        
        parentChildConsumer.accept(this, body);
        body.walk(parentChildConsumer);
    }
    
    @Override
    public void debugPrint(int depth)
    {
        IO.Debug.print("  ".repeat(depth));
        IO.Debug.println("FOR");
    
        IO.Debug.print("  ".repeat(depth + 1));
        IO.Debug.println("Counter: " + counterIdentifier.value().toString());
    
        IO.Debug.print("  ".repeat(depth + 1));
        IO.Debug.println("Initial Value: ");
        initialValue.debugPrint(depth + 2);
        
        IO.Debug.print("  ".repeat(depth + 1));
        IO.Debug.println("Max Value: ");
        maxValue.debugPrint(depth + 2);
    
        IO.Debug.print("  ".repeat(depth + 1));
        IO.Debug.println("Step: ");
        step.debugPrint(depth + 2);
        
        IO.Debug.print("  ".repeat(depth + 1));
        IO.Debug.println("Body: ");
        body.debugPrint(depth + 2);
    }
    
    @Override
    public Result<Void> interpret(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
        
        // Set Initial Value
        Result<Object> initialValueRaw = result.registerIssues(initialValue.getValue(interpreter));
        if (result.error() != null) return result;
        Result<?> initialValue = result.registerIssues(counter.getRuntimeType().tryCast(initialValueRaw.get()));
        if (result.error() != null) return result;
        result.registerIssues(counter.setValue(initialValue.get(), this));
    
        // Step Value
        Result<Object> stepValueRaw = result.registerIssues(step.getValue(interpreter));
        if (result.error() != null) return result;
        Result<Double> stepValue = result.registerIssues(RuntimeTypes.REAL.tryCast(stepValueRaw.get()));
        if (result.error() != null) return result;
        
        while (true)
        {
            // Counter Value
            Result<Object> counterValueRaw = result.registerIssues(counter.getValue(this));
            if (result.error() != null) return result;
            Result<Double> counterValue = result.registerIssues(RuntimeTypes.REAL.tryCast(counterValueRaw.get()));
            if (result.error() != null) return result;
            
            // Max Value
            Result<Object> maxValueRaw = result.registerIssues(maxValue.getValue(interpreter));
            if (result.error() != null) return result;
            Result<Double> maxValue = result.registerIssues(RuntimeTypes.REAL.tryCast(maxValueRaw.get()));
            if (result.error() != null) return result;
            
            // If counter meets or exceeds max, break
            if (stepValue.get() > 0 && counterValue.get() >= maxValue.get()) return result.success(null);
            else if (stepValue.get() < 0 && counterValue.get() <= maxValue.get()) return result.success(null);
            
            // Run Body
            result.register(body.interpret(interpreter));
    
            // Refresh Counter Value
            counterValueRaw = result.registerIssues(counter.getValue(this));
            if (result.error() != null) return result;
            counterValue = result.registerIssues(RuntimeTypes.REAL.tryCast(counterValueRaw.get()));
            if (result.error() != null) return result;
            
            // Increment Counter
            double newCounterValue = counterValue.get() + stepValue.get();
            if (counter.getRuntimeType().equals(RuntimeTypes.REAL)) result.registerIssues(counter.setValue(newCounterValue, this));
            else result.registerIssues(counter.setValue((long)newCounterValue, this));
            if (result.error() != null) return result;
        }
    }
}
