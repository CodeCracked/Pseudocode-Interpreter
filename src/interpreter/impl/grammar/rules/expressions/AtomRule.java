package interpreter.impl.grammar.rules.expressions;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.utils.Result;
import interpreter.core.parser.Parser;
import interpreter.impl.grammar.nodes.expressions.LiteralValueNode;
import interpreter.impl.grammar.nodes.expressions.UnaryOpNode;
import interpreter.impl.grammar.nodes.expressions.VariableAccessNode;
import interpreter.impl.grammar.rules.GrammarRules;
import interpreter.impl.tokens.TokenType;

public class AtomRule implements IGrammarRule
{
    @Override
    public Result<AbstractNode> build(Parser parser)
    {
        Result<AbstractNode> result = new Result<>();
        
        Token atom = parser.getCurrentToken();
    
        // Literals
        if (atom.type() == TokenType.STRING_LITERAL || atom.type() == TokenType.INTEGER_LITERAL || atom.type() == TokenType.REAL_LITERAL)
        {
            result.registerAdvancement();
            parser.advance();
            
            Result<LiteralValueNode> literal = LiteralValueNode.create(atom);
            if (literal.error() != null) return result.failure(literal.error());
            else return result.success(literal.get());
        }
        
        // Variable Identifiers
        else if (atom.type() == TokenType.IDENTIFIER)
        {
            result.registerAdvancement();
            parser.advance();
            return result.success(new VariableAccessNode(atom));
        }
        
        // Unary Operators
        else if (atom.type() == TokenType.MINUS)
        {
            result.registerAdvancement();
            parser.advance();
            
            AbstractNode argument = result.register(GrammarRules.ATOM.build(parser));
            if (result.error() != null) return result;
            
            Result<UnaryOpNode> unaryOp = UnaryOpNode.create(atom, (AbstractValuedNode) argument);
            if (unaryOp.error() != null) return result.failure(unaryOp.error());
            else return result.success(unaryOp.get());
        }
        
        // Parenthesis
        else if (atom.type() == TokenType.LPAREN)
        {
            result.registerAdvancement();
            parser.advance();
            
            AbstractNode expression = result.register(GrammarRules.EXPRESSION.build(parser));
            if (result.error() != null) return result;
            
            if (parser.getCurrentToken().type() != TokenType.RPAREN) return result.failure(new SyntaxException(parser, "Expected ')'!"));
            result.registerAdvancement();
            parser.advance();
            
            return result.success(expression);
        }
        
        else return result.failure(new SyntaxException(parser, "Expected literal, variable name, or '('!"));
    }
}
