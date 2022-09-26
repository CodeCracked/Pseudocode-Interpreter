package interpreter.impl.grammar.rules;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.ProgramNode;
import interpreter.impl.grammar.nodes.statements.ModuleDefinitionNode;
import interpreter.impl.tokens.TokenType;

import java.util.ArrayList;
import java.util.List;

public class ProgramRule implements IGrammarRule
{
    @Override
    public Result<AbstractNode> build(Parser parser)
    {
        Result<AbstractNode> result = new Result<>();
    
        List<ModuleDefinitionNode> modules = new ArrayList<>();
        
        if (parser.getCurrentToken().type() != TokenType.MODULE) return result.failure(new SyntaxException(parser, "Expected 'Module'!"));
        while (parser.getCurrentToken().type() == TokenType.MODULE)
        {
            ModuleDefinitionNode module = (ModuleDefinitionNode) result.register(GrammarRules.MODULE_DEFINITION.build(parser));
            if (result.error() != null) return result;
            else modules.add(module);
        }
    
        Token eof = parser.getCurrentToken();
        if (eof.type() != TokenType.EOF) return result.failure(new SyntaxException(parser, "Expected end-of-file!"));
        
        return result.success(new ProgramNode(modules));
    }
}
