package interpreter.impl;

import interpreter.core.Interpreter;
import interpreter.core.lexer.Lexer;
import interpreter.core.lexer.Token;
import interpreter.core.lexer.builders.IdentifierTokenBuilder;
import interpreter.core.lexer.builders.IndentTokenBuilder;
import interpreter.core.lexer.builders.KeywordTokenBuilder;
import interpreter.core.lexer.builders.MatcherTokenBuilder;
import interpreter.core.lexer.builders.NumberLiteralTokenBuilder;
import interpreter.core.lexer.builders.StringLiteralTokenBuilder;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.runtime.RuntimeType;
import interpreter.core.utils.IO;
import interpreter.impl.grammar.rules.GrammarRules;
import interpreter.impl.runtime.RuntimeTypes;
import interpreter.impl.tokens.KeywordLists;
import interpreter.impl.tokens.TokenType;

import java.util.List;

public class PseudocodeInterpreter extends Interpreter
{
    private final static Lexer lexer = new Lexer(TokenType.EOF,
            new KeywordTokenBuilder(TokenType.STATEMENT_KEYWORD, 1, KeywordLists.statementKeywords),
            new KeywordTokenBuilder(TokenType.TYPE_KEYWORD, 1, KeywordLists.typeKeywords),
            new MatcherTokenBuilder(TokenType.THEN, 1, "Then"),
            new MatcherTokenBuilder(TokenType.ELSE, 1, "Else"),
            
            new IdentifierTokenBuilder(TokenType.IDENTIFIER),
            
            new StringLiteralTokenBuilder(TokenType.STRING_LITERAL),
            new NumberLiteralTokenBuilder(TokenType.INTEGER_LITERAL, TokenType.REAL_LITERAL),
            
            new MatcherTokenBuilder(TokenType.ASSIGN, -1, "="),
            new MatcherTokenBuilder(TokenType.PLUS, -1, "+"),
            new MatcherTokenBuilder(TokenType.MINUS, -1, "-"),
            new MatcherTokenBuilder(TokenType.MUL, -1, "*"),
            new MatcherTokenBuilder(TokenType.DIV, -1, "/"),
            new MatcherTokenBuilder(TokenType.MOD, -1, "MOD"),
            new MatcherTokenBuilder(TokenType.POW, -1, "^"),
            
            new MatcherTokenBuilder(TokenType.EQUALS, -2, "=="),
            new MatcherTokenBuilder(TokenType.NOT_EQUALS, -2, "!="),
            new MatcherTokenBuilder(TokenType.GREATER, -1, ">"),
            new MatcherTokenBuilder(TokenType.LESS, -1, "<"),
            new MatcherTokenBuilder(TokenType.GREATER_EQUAL, -2, ">="),
            new MatcherTokenBuilder(TokenType.LESS_EQUAL, -2, "<="),
        
            new MatcherTokenBuilder(TokenType.AND, -2, "AND"),
            new MatcherTokenBuilder(TokenType.OR, -2, "OR"),
            new MatcherTokenBuilder(TokenType.NOT, -2, "NOT"),
            
            new MatcherTokenBuilder(TokenType.LPAREN, -1, "("),
            new MatcherTokenBuilder(TokenType.RPAREN, -1, ")"),
            new MatcherTokenBuilder(TokenType.COMMA, -1, ","),
            
            new MatcherTokenBuilder(TokenType.MODULE, 0, "Module"),
            new MatcherTokenBuilder(TokenType.END, 0, "End"),
            new MatcherTokenBuilder(TokenType.REF, 0, "Ref"),
            
            new IndentTokenBuilder(TokenType.INDENT, 4),
            new MatcherTokenBuilder(TokenType.NEWLINE, -1000, "\n")
    );
    private final static Parser parser = new Parser(GrammarRules.PROGRAM, TokenType.INDENT);
    
    private boolean debugMode;
    
    public PseudocodeInterpreter()
    {
        super(lexer, parser, "//");
    }
    
    public boolean isDebugMode() { return this.isDebugMode(); }
    public PseudocodeInterpreter setDebugMode(boolean debugMode)
    {
        this.debugMode = debugMode;
        return this;
    }
    
    @Override
    protected void registerTypes()
    {
        for (RuntimeType<?> type : RuntimeTypes.ALL_TYPES) RuntimeType.registerType(type);
    }
    
    @Override
    protected void onTokenize(List<Token> tokens)
    {
        if (debugMode)
        {
            for (Token token : tokens)
            {
                IO.Debug.print(token);
                if (token.type() == TokenType.NEWLINE || token.type() == TokenType.EOF) IO.Debug.println();
                else IO.Debug.print(" ");
            }
            IO.Debug.println();
        }
    }
    @Override
    protected void onBuildAST(AbstractNode ast)
    {
        if (debugMode)
        {
            ast.debugPrint(0);
            IO.Debug.println();
        }
    }
}
