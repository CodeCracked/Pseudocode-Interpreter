package interpreter.impl.grammar.rules.statements;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.flow.BlockNode;
import interpreter.impl.grammar.nodes.flow.BranchNode;
import interpreter.impl.grammar.rules.GrammarRules;
import interpreter.impl.tokens.TokenType;

public class IfStatementRule implements IGrammarRule
{
    @Override
    public Result<AbstractNode> build(Parser parser)
    {
        Result<AbstractNode> result = new Result<>();
        int indentation = parser.getCurrentIndent();
    
        // Root Statement
        BranchNode rootStatement = (BranchNode) result.register(ifClause(parser));
        if (result.error() != null) return result;
    
        // Else-If and Else Blocks
        boolean requireElseClause = false;
        boolean hasElseClause = false;
        while (parser.peekNextToken().type() == TokenType.ELSE)
        {
            // Indent
            if (!parser.getCurrentToken().matches(TokenType.INDENT, indentation)) return result.failure(new SyntaxException(parser, "Expected indentation of size " + indentation + "!"));
            result.registerAdvancement();
            parser.advance();
        
            // Else Keyword
            result.registerAdvancement();
            parser.advance();
        
            // Else-If Statement
            if (parser.getCurrentToken().isKeyword(TokenType.STATEMENT_KEYWORD, "If", 1))
            {
                BranchNode elseIf = (BranchNode) result.register(ifClause(parser));
                if (result.error() != null) return result;
                
                rootStatement.addElseIf(elseIf);
                requireElseClause = true;
            }
            
            // Else Statement
            else if (parser.getCurrentToken().type() == TokenType.NEWLINE)
            {
                result.registerAdvancement();
                parser.advance();
                
                // Else Block
                BlockNode elseBlock = (BlockNode) result.register(GrammarRules.block(parser));
                if (result.error() != null) return result;
                
                rootStatement.setElse(elseBlock);
                hasElseClause = true;
                
                break;
            }
            
            else return result.failure(new SyntaxException(parser, "Expected 'If' or newline!"));
        }
        
        // Indent
        if (!parser.getCurrentToken().matches(TokenType.INDENT, indentation)) return result.failure(new SyntaxException(parser, "Expected indentation of size " + indentation + "!"));
        result.registerAdvancement();
        parser.advance();
        
        // Check required Else clause
        if (requireElseClause && !hasElseClause) return result.failure(new SyntaxException(parser, "Expected 'Else'! Else clauses are required when using else-if clauses!"));
        
        // End
        if (parser.getCurrentToken().type() != TokenType.END) return result.failure(new SyntaxException(parser, "Expected 'End If'!"));
        result.registerAdvancement();
        parser.advance();
        
        // If
        Token closeToken = parser.getCurrentToken();
        if (!closeToken.matches(TokenType.STATEMENT_KEYWORD, "If")) return result.failure(new SyntaxException(parser, "Expected 'If'!"));
        result.registerAdvancement();
        parser.advance();
        rootStatement.setEnd(closeToken);
        
        // Newline
        if (parser.getCurrentToken().type() != TokenType.NEWLINE) return result.failure(new SyntaxException(parser, "Expected newline!"));
        result.registerAdvancement();
        parser.advance();
        
        return result.success(rootStatement);
    }
    
    private Result<AbstractNode> ifClause(Parser parser)
    {
        Result<AbstractNode> result = new Result<>();
        
        // If Keyword
        Token ifKeyword = parser.getCurrentToken();
        if (!ifKeyword.isKeyword(TokenType.STATEMENT_KEYWORD, "If", 1)) return result.failure(new SyntaxException(parser, "Expected 'If'!"));
        result.registerAdvancement();
        parser.advance();
        
        // Condition
        AbstractValuedNode condition = (AbstractValuedNode) result.register(GrammarRules.EXPRESSION.build(parser));
        if (result.error() != null) return result;
        
        // Then Keyword
        if (parser.getCurrentToken().type() != TokenType.THEN) return result.failure(new SyntaxException(parser, "Expected 'Then'!"));
        result.registerAdvancement();
        parser.advance();
        
        // Newline
        if (parser.getCurrentToken().type() != TokenType.NEWLINE) return result.failure(new SyntaxException(parser, "Expected newline!"));
        result.registerAdvancement();
        parser.advance();
        
        // True Block
        BlockNode trueNode = (BlockNode) result.register(GrammarRules.block(parser));
        if (result.error() != null) return result;
        
        return result.success(new BranchNode(ifKeyword, condition, trueNode));
    }
}
