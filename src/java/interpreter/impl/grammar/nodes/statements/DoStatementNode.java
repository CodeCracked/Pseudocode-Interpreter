package interpreter.impl.grammar.nodes.statements;

import interpreter.core.Interpreter;
import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.utils.IO;
import interpreter.core.utils.Result;
import interpreter.impl.runtime.RuntimeTypes;
import interpreter.impl.tokens.TokenType;

import java.util.function.BiConsumer;

public class DoStatementNode extends AbstractNode
{
    private final AbstractNode body;
    private final Token conditionType;
    private final AbstractValuedNode condition;
    
    private boolean breakOnTrue;
    
    public DoStatementNode(Token doKeyword, AbstractNode body, Token conditionType, AbstractValuedNode condition)
    {
        super(doKeyword.start(), condition.end());
        
        this.condition = condition;
        this.conditionType = conditionType;
        this.body = body;
    }
    
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
    
        // Body
        result.register(body.populate(interpreter));
        if (result.error() != null) return result;
        
        // Condition Type
        if (conditionType.type() == TokenType.UNTIL) breakOnTrue = true;
        else if (conditionType.isKeyword(TokenType.STATEMENT_KEYWORD, "While")) breakOnTrue = false;
        else return result.failure(new SyntaxException(conditionType, "Expected 'While' or 'Until'!"));
        
        // Condition
        result.register(condition.populate(interpreter));
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
            // Run Body
            result.register(body.interpret(interpreter));
            if (result.error() != null) return result;
            
            // Check Break Condition
            Result<Object> conditionResult = result.registerIssues(condition.getValue(interpreter));
            if (result.error() != null) return result;
            Result<Boolean> casted = result.registerIssues(RuntimeTypes.BOOLEAN.tryCast(conditionResult.get()));
            if (result.error() != null) return result;
            if (casted.get() == breakOnTrue) return result.success(null);
        }
    }
}
