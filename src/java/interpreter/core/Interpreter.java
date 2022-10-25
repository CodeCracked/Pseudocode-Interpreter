package interpreter.core;

import interpreter.core.lexer.Lexer;
import interpreter.core.lexer.Token;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.source.SourceCollection;
import interpreter.core.source.SourcePosition;
import interpreter.core.utils.Result;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class Interpreter
{
    private final Lexer lexer;
    private final Parser parser;
    private final String commentStartRegex;
    
    public Interpreter(Lexer lexer, Parser parser, String commentStartRegex)
    {
        this.lexer = lexer;
        this.parser = parser;
        this.commentStartRegex = commentStartRegex;
        registerTypes();
    }
    
    protected void registerTypes() { }
    protected void onTokenize(List<Token> tokens) { }
    protected void onBuildAST(AbstractNode ast) { }
    
    public Result<AbstractNode> runFile(Path file, Consumer<Boolean> finishedCallback)
    {
        Result<AbstractNode> result = new Result<>();

        // Load Source Collection
        SourceCollection source = SourceCollection.createFromFile(file, commentStartRegex);
        if (source == null)
        {
            finishedCallback.accept(false);
            return result.failure(new IllegalArgumentException("Could not load source collection " + file.toString()));
        }
        SourcePosition position = new SourcePosition(source);

        // Tokenize Pseudocode Source
        Result<List<Token>> lexerResult = result.registerIssues(lexer.tokenize(position));
        lexerResult.displayIssues();
        if (result.error() != null)
        {
            finishedCallback.accept(false);
            return result;
        }
        onTokenize(lexerResult.get());

        // Generate AST from Token List
        Result<AbstractNode> parseResult = result.registerIssues(parser.parse(this, lexerResult.get()));
        parseResult.displayIssues();
        if (result.error() != null)
        {
            finishedCallback.accept(false);
            return result;
        }
        AbstractNode ast = parseResult.get();
        onBuildAST(ast);

        runAST(ast, finishedCallback);
        return result.success(ast);
    }
    public void runAST(AbstractNode ast, Consumer<Boolean> finishedCallback)
    {
        Thread thread = new Thread(() ->
        {
            // Interpret AST
            Result<Void> interpretationResult = ast.interpret(this);
            interpretationResult.displayIssues();
            if (interpretationResult.error() != null)
            {
                finishedCallback.accept(false);
                return;
            }

            finishedCallback.accept(true);
        });
        thread.setName("InterpreterThread");
        thread.start();
    }
}
