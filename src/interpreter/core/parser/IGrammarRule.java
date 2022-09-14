package interpreter.core.parser;

import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.utils.Result;

public interface IGrammarRule
{
    Result<AbstractNode> build(Parser parser);
}
