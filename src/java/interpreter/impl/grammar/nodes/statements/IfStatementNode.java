package interpreter.impl.grammar.nodes.statements;

import interpreter.core.Interpreter;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.utils.IO;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.blocks.BlockNode;
import interpreter.impl.runtime.RuntimeTypes;

import java.util.function.BiConsumer;

public class IfStatementNode extends AbstractNode
{
    private final AbstractValuedNode condition;
    private final BlockNode trueNode;
    private AbstractNode falseNode;
    
    public IfStatementNode(Token ifKeyword, AbstractValuedNode condition, BlockNode trueNode)
    {
        super(ifKeyword.start(), trueNode.end());
        this.condition = condition;
        this.trueNode = trueNode;
    }
    
    public Result<AbstractNode> addElseIf(IfStatementNode elseIfStatement)
    {
        if (falseNode == null)
        {
            this.falseNode = elseIfStatement;
            return Result.of(this);
        }
        else if (falseNode instanceof IfStatementNode elseIf) return elseIf.addElseIf(elseIfStatement);
        else return Result.fail(new IllegalStateException("Trying to add else-if statement to an if-statement that already has an else block!"));
    }
    public Result<AbstractNode> setElse(BlockNode elseBody)
    {
        if (falseNode == null)
        {
            this.falseNode = elseBody;
            return Result.of(this);
        }
        else if (falseNode instanceof IfStatementNode elseIf) return elseIf.setElse(elseBody);
        else return Result.fail(new IllegalStateException("Trying to add an else block to an if-statement that already has an else block!"));
    }
    public void setEnd(Token closeToken)
    {
        this.endPosition = closeToken.end();
    }
    
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
        
        // Condition
        result.register(condition.populate(interpreter));
        if (result.error() != null) return result;
        
        // True Node
        result.register(trueNode.populate(interpreter));
        if (result.error() != null) return result;
    
        // False Node
        result.register(falseNode.populate(interpreter));
        if (result.error() != null) return result;
        
        return result.success(null);
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer)
    {
        // Condition
        parentChildConsumer.accept(this, condition);
        condition.walk(parentChildConsumer);
    
        // True Node
        parentChildConsumer.accept(this, trueNode);
        trueNode.walk(parentChildConsumer);
    
        // False Node
        parentChildConsumer.accept(this, falseNode);
        falseNode.walk(parentChildConsumer);
    }
    
    @Override
    public void debugPrint(int depth)
    {
        IO.Debug.print("  ".repeat(depth));
        IO.Debug.println("IF");
        
        IO.Debug.print("  ".repeat(depth + 1));
        IO.Debug.println("Condition:");
        condition.debugPrint(depth + 2);
    
        IO.Debug.print("  ".repeat(depth + 1));
        IO.Debug.println("True:");
        trueNode.debugPrint(depth + 2);
    
        IO.Debug.print("  ".repeat(depth + 1));
        IO.Debug.println("False:");
        falseNode.debugPrint(depth + 2);
    }
    
    @Override
    public Result<Void> interpret(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
        
        // Condition Result
        Result<Object> conditionResult = condition.getValue(interpreter);
        if (conditionResult.error() != null) return result.failure(conditionResult.error());
        Result<Boolean> casted = RuntimeTypes.BOOLEAN.tryCast(conditionResult.get());
        if (casted.error() != null) return result.failure(casted.error());
        
        // True Node
        if (casted.get()) result.register(trueNode.interpret(interpreter));
        
        // False Node
        else result.register(falseNode.interpret(interpreter));
        
        return result;
    }
}
