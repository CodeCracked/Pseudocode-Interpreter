package interpreter.core;

import interpreter.core.lexer.Lexer;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.utils.Result;
import interpreter.core.parser.Parser;
import interpreter.core.source.SourceCollection;
import interpreter.core.source.SourcePosition;
import interpreter.core.utils.Printing;

import java.nio.file.Path;
import java.util.List;

public class Interpreter
{
    private final Lexer lexer;
    private final Parser parser;
    
    public Interpreter(Lexer lexer, Parser parser)
    {
        this.lexer = lexer;
        this.parser = parser;
        registerTypes();
    }
    
    protected void registerTypes() { }
    protected void onTokenize(List<Token> tokens) { }
    protected void onBuildAST(AbstractNode ast) { }
    
    public void runFile(Path file)
    {
        // Load Source Collection
        SourceCollection source = SourceCollection.createFromFile(file);
        if (source == null) return;
        SourcePosition position = new SourcePosition(source);
    
        // Tokenize Pseudocode Source
        List<Token> tokens = lexer.tokenize(position);
        onTokenize(tokens);
        
        // Generate AST from Token List
        Result<AbstractNode> parseResult = parser.parse(this, tokens);
        if (parseResult.error() != null)
        {
            Printing.Errors.println(parseResult.error().getMessage());
            return;
        }
        AbstractNode ast = parseResult.get();
        onBuildAST(ast);
        
        // Interpret AST
        Result<Void> interpretationResult = ast.interpret(this);
        if (interpretationResult.error() != null) Printing.Errors.println(interpretationResult.error().getMessage());
    }
}
