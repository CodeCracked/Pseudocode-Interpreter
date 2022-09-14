package interpreter.core.parser;

import interpreter.core.lexer.Token;
import interpreter.core.utils.Printing;

import java.util.List;
import java.util.Stack;

public class Parser
{
    private final List<Token> tokens;
    private final IGrammarRule programRule;
    private final Enum<?> indentTokenType;
    private int tokenIndex;
    private Token currentToken;
    private Token nextToken;
    private int currentIndent;
    
    private final Stack<Integer> revertStack;
    
    public Parser(List<Token> tokens, IGrammarRule programRule, Enum<?> indentTokenType)
    {
        this.tokens = tokens;
        this.programRule = programRule;
        this.indentTokenType = indentTokenType;
        this.tokenIndex = -1;
        this.currentIndent = 0;
        advance();
        
        this.revertStack = new Stack<>();
    }
    
    public void advance()
    {
        this.tokenIndex++;
        this.currentToken = tokenIndex < tokens.size() ? tokens.get(tokenIndex) : null;
        this.nextToken = (tokenIndex + 1) < tokens.size() ? tokens.get(tokenIndex + 1) : null;
        
        if (indentTokenType != null && currentToken != null && currentToken.type() == indentTokenType) this.currentIndent = (int)currentToken.value();
    }
    public ParseResult parse()
    {
        ParseResult result = programRule.build(this);
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
