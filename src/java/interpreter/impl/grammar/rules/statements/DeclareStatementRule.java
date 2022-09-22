package interpreter.impl.grammar.rules.statements;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.statements.DeclareStatementNode;
import interpreter.impl.grammar.rules.GrammarRules;
import interpreter.impl.tokens.TokenType;

public class DeclareStatementRule implements IGrammarRule
{
    @Override
    public Result<AbstractNode> build(Parser parser)
    {
        Result<AbstractNode> result = new Result<>();
    
        // Keyword
        Token keyword = parser.getCurrentToken();
        if (!keyword.isKeyword(TokenType.STATEMENT_KEYWORD, "Declare", 1)) return result.failure(new SyntaxException(parser, "Expected keyword 'Declare'! Did you forget proper capitalization?"));
        result.registerAdvancement();
        parser.advance();
        
        // Data Type
        Token dataType = parser.getCurrentToken();
        if (dataType.type() != TokenType.TYPE_KEYWORD) return result.failure(new SyntaxException(parser, "Expected data type, found " + dataType.type().name() +  "! Did you forget proper capitalization?"));
        else if (dataType.trailingSpaces() < 1) return result.failure(new SyntaxException(parser, "You forgot to add a space after the data type!"));
        result.registerAdvancement();
        parser.advance();
        
        // Identifier
        Token identifier = parser.getCurrentToken();
        if (identifier.type() != TokenType.IDENTIFIER) return result.failure(new SyntaxException(parser, "Expected identifier, found " + identifier.type().name() + "!"));
        result.registerAdvancement();
        parser.advance();
        
        // Optional Initializer
        AbstractNode initialValue = null;
        Token assignmentOperator = parser.getCurrentToken();
        if (assignmentOperator.type() == TokenType.ASSIGN)
        {
            result.registerAdvancement();
            parser.advance();
            
            initialValue = result.register(GrammarRules.EXPRESSION.build(parser));
            if (result.error() != null) return result;
        }
        
        // Newline
        if (parser.getCurrentToken().type() != TokenType.NEWLINE) return result.failure(new SyntaxException(parser, "Expected newline!"));
        result.registerAdvancement();
        parser.advance();
        
        // Create Node
        return result.success(new DeclareStatementNode(keyword, dataType, identifier, (AbstractValuedNode) initialValue));
    }
}
