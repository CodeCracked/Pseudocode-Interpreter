package interpreter.impl.grammar.nodes.statements;

import interpreter.core.Interpreter;
import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.runtime.VariableSymbol;
import interpreter.core.utils.Printing;
import interpreter.core.utils.Result;
import interpreter.impl.runtime.SymbolType;

import java.util.Scanner;
import java.util.function.BiConsumer;

public class InputStatementNode extends AbstractNode
{
    private static final Scanner inputSource = new Scanner(System.in);
    
    private final String identifier;
    
    private VariableSymbol symbol;
    
    public InputStatementNode(Token keyword, Token identifier)
    {
        super(keyword.start(), identifier.end());
        this.identifier = identifier.value().toString();
    }
    
    @Override
    public Result<Void> populate(Interpreter interpreter)
    {
        Result<Void> result = new Result<>();
        
        // Variable Symbol
        symbol = getSymbolTable().getSymbol(SymbolType.VARIABLE, identifier);
        if (symbol == null) return Result.fail(new SyntaxException(this, "Cannot find variable '" + identifier + "'! Are you sure it was spelled and capitalized correctly?"));
        
        return result.success(null);
    }
    
    @Override
    public void walk(BiConsumer<AbstractNode, AbstractNode> parentChildConsumer) { }
    
    @Override
    public void debugPrint(int depth)
    {
        Printing.Debug.print("  ".repeat(depth));
        Printing.Debug.println("INPUT " + identifier + "");
    }
    
    @Override
    public Result<Void> interpret(Interpreter interpreter)
    {
        String inputStr = inputSource.nextLine();
        
        Result<?> parsed = symbol.getRuntimeType().tryParse(inputStr);
        if (parsed.error() != null) return Result.fail(parsed.error());
        
        Result<?> setResult = symbol.setValue(parsed.get());
        if (setResult.error() != null) return Result.fail(setResult.error());
        
        return Result.of(null);
    }
}
