package interpreter.impl.grammar.rules;

import interpreter.core.parser.IGrammarRule;
import interpreter.impl.grammar.rules.statements.DisplayStatementRule;

public class GrammarRules
{
    public static IGrammarRule PROGRAM = new ProgramRule();
    
    public static IGrammarRule DISPLAY_STATEMENT = new DisplayStatementRule();
}
