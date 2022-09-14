package main;

import interpreter.core.Interpreter;
import interpreter.core.lexer.Lexer;
import interpreter.core.lexer.Token;
import interpreter.core.lexer.builders.IdentifierTokenBuilder;
import interpreter.core.lexer.builders.KeywordTokenBuilder;
import interpreter.core.lexer.builders.MatcherTokenBuilder;
import interpreter.core.lexer.builders.StringLiteralTokenBuilder;
import interpreter.core.parser.AbstractNode;
import interpreter.core.parser.Parser;
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
    private static Lexer lexer = new Lexer(TokenType.EOF,
            new KeywordTokenBuilder(TokenType.STATEMENT_KEYWORD, 1, KeywordLists.statementKeywords),
            new IdentifierTokenBuilder(TokenType.IDENTIFIER),
            new StringLiteralTokenBuilder(TokenType.STRING_LITERAL),
            new MatcherTokenBuilder(TokenType.NEWLINE, -1000, "\n", false)
    );
    private static Parser parser = new Parser(GrammarRules.PROGRAM, null);
    private static Interpreter interpreter = new Interpreter(lexer, parser)
    {
        @Override
        protected void onTokenize(List<Token> tokens)
        {
            for (Token token : tokens)
            {
                Printing.Debug.print(token);
                if (token.type() == TokenType.NEWLINE || token.type() == TokenType.EOF) Printing.Debug.println();
                else Printing.Debug.print(" ");
            }
            Printing.Debug.println();
        }
        @Override
        protected void onBuildAST(AbstractNode ast)
        {
            ast.debugPrint(0);
            Printing.Debug.println();
        }
    };
    
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
        interpreter.runFile(filePath);
    }
}