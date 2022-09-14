package main;

import interpreter.core.lexer.Lexer;
import interpreter.core.lexer.Token;
import interpreter.core.lexer.builders.IdentifierTokenBuilder;
import interpreter.core.lexer.builders.KeywordTokenBuilder;
import interpreter.core.lexer.builders.MatcherTokenBuilder;
import interpreter.core.lexer.builders.StringLiteralTokenBuilder;
import interpreter.core.parser.AbstractNode;
import interpreter.core.parser.ParseResult;
import interpreter.core.parser.Parser;
import interpreter.core.source.SourceCollection;
import interpreter.core.source.SourcePosition;
import interpreter.core.utils.Printing;
import interpreter.impl.grammar.rules.GrammarRules;
import interpreter.impl.tokens.KeywordLists;
import interpreter.impl.tokens.TokenType;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        if (args.length > 0)
        {
            for (String arg : args)
            {
                Path path = Paths.get(arg);
                if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) interpretFile(path);
            }
        }
    }
    
    private static void interpretFile(Path filePath)
    {
        SourceCollection source = SourceCollection.createFromFile(filePath);
        if (source == null) return;
        SourcePosition position = new SourcePosition(source);
    
        // Tokenize Pseudocode Source
        Lexer lexer = new Lexer(
                new KeywordTokenBuilder(TokenType.STATEMENT_KEYWORD, 1, KeywordLists.statementKeywords),
                new IdentifierTokenBuilder(TokenType.IDENTIFIER),
                new StringLiteralTokenBuilder(TokenType.STRING_LITERAL),
                new MatcherTokenBuilder(TokenType.NEWLINE, -1000, "\n", false)
        );
        List<Token> tokens = lexer.tokenize(position, TokenType.EOF);
        for (Token token : tokens)
        {
            Printing.Debug.print(token);
            if (token.type() == TokenType.NEWLINE || token.type() == TokenType.EOF) Printing.Debug.println();
            else Printing.Debug.print(" ");
        }
        Printing.Debug.println();
        
        // Generate AST from Token List
        Parser parser = new Parser(tokens, GrammarRules.PROGRAM, null);
        ParseResult parseResult = parser.parse();
        if (parseResult.error() != null)
        {
            Printing.Errors.println(parseResult.error().getMessage());
            return;
        }
        AbstractNode ast = parseResult.node();
        ast.debugPrint(0);
    }
}