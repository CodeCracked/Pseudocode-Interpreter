package interpreter.core.exceptions;

import interpreter.core.parser.Parser;

public class SyntaxException extends InterpreterException
{
    public SyntaxException(Parser parser, String reason)
    {
        super(parser.getCurrentToken().start(), parser.getCurrentToken().end(), "Syntax Error", reason);
    }
}
