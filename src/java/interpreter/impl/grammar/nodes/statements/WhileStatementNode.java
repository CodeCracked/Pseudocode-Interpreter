package interpreter.impl.grammar.nodes.statements;

import interpreter.core.Interpreter;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.utils.IO;
import interpreter.core.utils.Result;
import interpreter.impl.runtime.RuntimeTypes;

import java.util.function.BiConsumer;

public class WhileStatementNode extends AbstractNode
{
    private final AbstractValuedNode condition;
    private final AbstractNode body;
    
    public WhileStatementNode(Token whileKeyword, AbstractValuedNode condition, AbstractNode body, Token closeToken)
    {
        super(whileKeyword.start(), closeToken.end());
        
        this.condition = condition;
        this.body = body;
    }
    
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
        
        // Condition
        result.register(condition.populate(interpreter));
        if (result.error() != null) return result;
        
        // Body
        result.register(body.populate(interpreter));
        if (result.error() != null) return result;
        
        return result.success(null);
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer)
    {
        parentChildConsumer.accept(this, condition);
        condition.walk(parentChildConsumer);
        
        parentChildConsumer.accept(this, body);
        body.walk(parentChildConsumer);
    }
    
    @Override
    public void debugPrint(int depth)
    {
        IO.Debug.print("  ".repeat(depth));
        IO.Debug.println("WHILE");
    
        IO.Debug.print("  ".repeat(depth + 1));
        IO.Debug.println("Condition: ");
        condition.debugPrint(depth + 2);
    
        IO.Debug.print("  ".repeat(depth + 1));
        IO.Debug.println("Body: ");
        body.debugPrint(depth + 2);
    }
    
    @Override
    public Result<Void> interpret(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
    
        while (true)
        {
            // Condition Result
            Result<Object> conditionResult = result.registerIssues(condition.getValue(interpreter));
            if (result.error() != null) return result;
            Result<Boolean> casted = result.registerIssues(RuntimeTypes.BOOLEAN.tryCast(conditionResult.get()));
            if (result.error() != null) return result;
            
            // Run body if condition is true, else break
            if (casted.get())
            {
                result.register(body.interpret(interpreter));
                if (result.error() != null) return result;
            }
            else return result.success(null);
        }
    }
}
