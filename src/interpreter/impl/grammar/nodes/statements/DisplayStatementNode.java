package interpreter.impl.grammar.nodes.statements;

import interpreter.core.Interpreter;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.utils.Printing;
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
        Printing.Debug.print("  ".repeat(depth));
        Printing.Debug.println("DISPLAY:");
        messagePieces.debugPrint(depth + 1);
    }
    @Override
    public Result<Void> interpret(Interpreter interpreter)
    {
        for (AbstractValuedNode messagePiece : messagePieces.values)
        {
            Result<Object> pieceValue = messagePiece.getValue(interpreter);
            if (pieceValue.error() != null) return Result.fail(pieceValue.error());
            Printing.Output.print(pieceValue.get());
        }
        Printing.Output.println();
        
        return Result.of(null);
    }
}
