package interpreter.impl.grammar.rules.components;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.components.ParameterNode;
import interpreter.impl.grammar.nodes.components.ParameterListNode;
import interpreter.impl.tokens.TokenType;

import java.util.ArrayList;
import java.util.List;

public class ParameterListRule implements IGrammarRule
{
    @Override
    public Result<AbstractNode> build(Parser parser)
    {
        Result<AbstractNode> result = new Result<>();
        List<ParameterNode> parameters = new ArrayList<>();
        
        // Right Parenthesis
        Token leftParenthesis = parser.getCurrentToken();
        if (leftParenthesis.type() != TokenType.LPAREN) return result.failure(new SyntaxException(leftParenthesis, "Expected '('!"));
        result.registerAdvancement();
        parser.advance();
        
        // Check Empty Argument Set
        if (parser.getCurrentToken().type() == TokenType.RPAREN)
        {
            Token rightParenthesis = parser.getCurrentToken();
            result.registerAdvancement();
            parser.advance();
            return result.success(new ParameterListNode(leftParenthesis, parameters, rightParenthesis));
        }
        
        // Build First Argument
        ParameterNode argument = (ParameterNode) result.register(buildArgument(parser));
        if (result.error() != null) return result;
        else parameters.add(argument);
        
        // Build Additional Arguments
        while (parser.getCurrentToken().type() == TokenType.COMMA)
        {
            result.registerAdvancement();
            parser.advance();
    
            argument = (ParameterNode) result.register(buildArgument(parser));
            if (result.error() != null) return result;
            else parameters.add(argument);
        }
        
        // Right Parenthesis
        Token rightParenthesis = parser.getCurrentToken();
        if (rightParenthesis.type() != TokenType.RPAREN) return result.failure(new SyntaxException(parser, "Expected ')'!"));
        result.registerAdvancement();
        parser.advance();
        
        return result.success(new ParameterListNode(leftParenthesis, parameters, rightParenthesis));
    }
    
    private Result<AbstractNode> buildArgument(Parser parser)
    {
        Result<AbstractNode> result = new Result<>();
    
        // Data Type
        Token dataType = parser.getCurrentToken();
        if (dataType.type() != TokenType.TYPE_KEYWORD) return result.failure(new SyntaxException(parser, "Expected data type, found " + dataType.type().name() +  "! Did you forget proper capitalization?"));
        else if (dataType.trailingSpaces() < 1) return result.failure(new SyntaxException(parser, "You forgot to add a space after the data type!"));
        result.registerAdvancement();
        parser.advance();
        
        // Identifier
        Token identifier = parser.getCurrentToken();
        if (identifier.type() != TokenType.IDENTIFIER) return result.failure(new SyntaxException(parser, "Expected identifier!"));
        result.registerAdvancement();
        parser.advance();
        
        return result.success(new ParameterNode(dataType, identifier));
    }
}
