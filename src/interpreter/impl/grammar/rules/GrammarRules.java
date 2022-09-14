package interpreter.impl.grammar.rules;

import interpreter.core.parser.IGrammarRule;
import interpreter.impl.grammar.rules.blocks.BlockRule;
import interpreter.impl.grammar.rules.expressions.AtomRule;
import interpreter.impl.grammar.rules.statements.DeclareStatementRule;
import interpreter.impl.grammar.rules.statements.DisplayStatementRule;
import interpreter.impl.grammar.rules.statements.StatementRule;

public class GrammarRules
{
    public static IGrammarRule PROGRAM = new ProgramRule();
    
    public static IGrammarRule BLOCK = new BlockRule();
    
    public static IGrammarRule STATEMENT = new StatementRule();
    public static IGrammarRule DECLARE_STATEMENT = new DeclareStatementRule();
    public static IGrammarRule DISPLAY_STATEMENT = new DisplayStatementRule();
    
    public static IGrammarRule ATOM = new AtomRule();
}
