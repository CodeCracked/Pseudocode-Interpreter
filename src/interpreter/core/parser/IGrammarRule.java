package interpreter.core.parser;

public interface IGrammarRule
{
    ParseResult build(Parser parser);
}
