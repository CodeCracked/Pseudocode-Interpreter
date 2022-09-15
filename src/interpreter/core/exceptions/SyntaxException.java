package interpreter.core.exceptions;

import interpreter.core.lexer.Token;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.source.SourcePosition;

public class SyntaxException extends InterpreterException
{
    public SyntaxException(Token token, String reason)
    {
        this(token.start(), token.end(), reason);
    }
    public SyntaxException(AbstractNode node, String reason)
    {
        this(node.start(), node.end(), reason);
    }
    public SyntaxException(Parser parser, String reason)
    {
        this(parser.getCurrentToken().start(), parser.getCurrentToken().end(), reason);
    }
    public SyntaxException(SourcePosition start, SourcePosition end, String reason)
    {
        super(start, end, "Syntax Error", reason);
    }
}
