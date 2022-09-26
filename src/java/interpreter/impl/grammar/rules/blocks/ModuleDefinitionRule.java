package interpreter.impl.grammar.rules.blocks;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.flow.BlockNode;
import interpreter.impl.grammar.nodes.statements.ModuleDefinitionNode;
import interpreter.impl.grammar.nodes.components.ParameterListNode;
import interpreter.impl.grammar.rules.GrammarRules;
import interpreter.impl.tokens.TokenType;

public class ModuleDefinitionRule implements IGrammarRule
{
    @Override
    public Result<AbstractNode> build(Parser parser)
    {
        Result<AbstractNode> result = new Result<>();
        
        // Keyword
        Token openKeyword = parser.getCurrentToken();
        if (openKeyword.type() != TokenType.MODULE) return result.failure(new SyntaxException(parser, "Expected 'Module'!"));
        result.registerAdvancement();
        parser.advance();
        
        // Identifier
        Token identifier = parser.getCurrentToken();
        if (identifier.type() != TokenType.IDENTIFIER) return result.failure(new SyntaxException(parser, "Expected identifier!"));
        result.registerAdvancement();
        parser.advance();
        
        // Parameter List
        ParameterListNode parameters = (ParameterListNode) result.register(GrammarRules.PARAMETER_LIST.build(parser));
        if (result.error() != null) return result;
        
        // Newline
        if (parser.getCurrentToken().type() != TokenType.NEWLINE) return result.failure(new SyntaxException(parser, "Expected newline!"));
        result.registerAdvancement();
        parser.advance();
        
        // Body
        BlockNode body = (BlockNode) result.register(GrammarRules.block(parser, 1));
        if (result.error() != null) return result;
        
        // End Keyword
        if (parser.getCurrentToken().type() != TokenType.END) return result.failure(new SyntaxException(parser, "Expected 'End'!"));
        result.registerAdvancement();
        parser.advance();
    
        // Module Keyword
        if (parser.getCurrentToken().type() != TokenType.MODULE) return result.failure(new SyntaxException(parser, "Expected 'Module'!"));
        result.registerAdvancement();
        parser.advance();
    
        // End Keyword
        Token closeKeyword = parser.getCurrentToken();
        if (closeKeyword.type() != TokenType.NEWLINE) return result.failure(new SyntaxException(parser, "Expected newline!"));
        result.registerAdvancement();
        parser.advance();
        
        return result.success(new ModuleDefinitionNode(openKeyword, identifier, parameters, body, closeKeyword));
    }
}
