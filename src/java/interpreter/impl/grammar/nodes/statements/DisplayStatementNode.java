package interpreter.impl.grammar.nodes.statements;

import interpreter.core.Interpreter;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.utils.IO;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.components.ValueSetNode;

import java.util.function.BiConsumer;

public class DisplayStatementNode extends AbstractNode
{
    private final ValueSetNode messagePieces;
    
    public DisplayStatementNode(Token keyword, ValueSetNode messagePieces)
    {
        super(keyword.start(), messagePieces.end());
        this.messagePieces = messagePieces;
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer)
    {
        parentChildConsumer.accept(this, messagePieces);
        messagePieces.walk(parentChildConsumer);
    }
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        return messagePieces.populate(interpreter);
    }
    @Override
    public void debugPrint(int depth)
    {
        IO.Debug.print("  ".repeat(depth));
        IO.Debug.println("DISPLAY:");
        messagePieces.debugPrint(depth + 1);
    }
    @Override
    public Result<Void> interpret(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();

        for (AbstractValuedNode messagePiece : messagePieces.values)
        {
            Result<Object> pieceValue = result.registerIssues(messagePiece.getValue(interpreter));
            if (result.error() != null) return result;
            IO.Output.print(pieceValue.get());
        }
        IO.Output.println();
        
        return result.success(null);
    }
}
