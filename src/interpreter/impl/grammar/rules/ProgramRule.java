package interpreter.impl.grammar.rules;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.utils.Result;
import interpreter.core.parser.Parser;
import interpreter.impl.grammar.nodes.ProgramNode;
import interpreter.impl.tokens.TokenType;

public class ProgramRule implements IGrammarRule
{
    @Override
    public Result<AbstractNode> build(Parser parser)
    {
        Result<AbstractNode> result = new Result<>();
        
        AbstractNode display = result.register(GrammarRules.BLOCK.build(parser));
        if (result.error() != null) return result;
    
        Token eof = parser.getCurrentToken();
        if (eof.type() != TokenType.EOF) return result.failure(new SyntaxException(parser, "Expected end-of-file!"));
        
        return result.success(new ProgramNode(display));
    }
}
