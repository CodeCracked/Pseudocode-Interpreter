package interpreter.core.parser;

import interpreter.core.lexer.Token;
import interpreter.core.utils.Printing;

import java.util.List;
import java.util.Stack;

public class Parser
{
    private final IGrammarRule programRule;
    private final Enum<?> indentTokenType;
    
    private List<Token> tokens;
    private int tokenIndex;
    private Token currentToken;
    private Token nextToken;
    private int currentIndent;
    
    private final Stack<Integer> revertStack;
    
    public Parser(IGrammarRule programRule, Enum<?> indentTokenType)
    {
        this.programRule = programRule;
        this.indentTokenType = indentTokenType;
        this.revertStack = new Stack<>();
    }
    
    public void advance()
    {
        this.tokenIndex++;
        this.currentToken = tokenIndex < tokens.size() ? tokens.get(tokenIndex) : null;
        this.nextToken = (tokenIndex + 1) < tokens.size() ? tokens.get(tokenIndex + 1) : null;
        
        if (indentTokenType != null && currentToken != null && currentToken.type() == indentTokenType) this.currentIndent = (int)currentToken.value();
    }
    public ParseResult parse(List<Token> tokens)
    {
        this.tokens = tokens;
        this.tokenIndex = -1;
        this.currentIndent = 0;
        this.revertStack.clear();
        advance();
        
        ParseResult result = programRule.build(this);
        if (result.error() != null) return result;
        
        result.node().createSymbolTable();
        result.node().walk((parent, child) ->
                {
                    child.createSymbolTable();
                    child.parent = parent;
                });
        
        // TODO: Check for trailing tokens
        //if (result.error() == null && currentToken.type() != TokenType.EOF) return result.failure(new MCLSyntaxError(source.getCodeLocation(currentToken.startPosition()),
        //        source.getCodeLocation(currentToken.endPosition()), "Expected 'namespace'"));
        return result;
    }
    
    // region Position Returning
    public void markPosition()
    {
        revertStack.push(tokenIndex);
    }
    public void unmarkPosition()
    {
        if (revertStack.size() == 0)
        {
            Printing.Errors.println("Trying to revert position of Parser when no positions have been marked!");
            throw new IllegalStateException("Trying to revert position of Parser when no positions have been marked!");
        }
        else this.revertStack.pop();
    }
    public void revertPosition()
    {
        if (revertStack.size() == 0)
        {
            Printing.Errors.println("Trying to revert position of Parser when no positions have been marked!");
            throw new IllegalStateException("Trying to revert position of Parser when no positions have been marked!");
        }
        else
        {
            this.tokenIndex = revertStack.pop() - 1;
            advance();
        }
    }
    // endregion
    //region Getters
    public Token getCurrentToken() { return currentToken; }
    public Token peekNextToken() { return nextToken; }
    public int getTokenIndex() { return tokenIndex; }
    public int getCurrentIndent() { return currentIndent; }
    //endregion
}
