package interpreter.impl.grammar.rules.statements;

import interpreter.core.exceptions.SyntaxException;
import interpreter.core.lexer.Token;
import interpreter.core.parser.IGrammarRule;
import interpreter.core.parser.Parser;
import interpreter.core.parser.nodes.AbstractNode;
import interpreter.core.parser.nodes.AbstractValuedNode;
import interpreter.core.utils.Result;
import interpreter.impl.grammar.nodes.expressions.BinaryOpNode;
import interpreter.impl.grammar.nodes.flow.BranchNode;
import interpreter.impl.grammar.rules.GrammarRules;
import interpreter.impl.tokens.TokenType;

public class SwitchStatementRule implements IGrammarRule
{
    @Override
    public Result<AbstractNode> build(Parser parser)
    {
        Result<AbstractNode> result = new Result<>();
        int caseIndent = parser.getCurrentIndent() + 1;
        
        // Select Keyword
        Token selectKeyword = parser.getCurrentToken();
        if (!selectKeyword.isKeyword(TokenType.STATEMENT_KEYWORD, "Select")) return result.failure(new SyntaxException(parser, "Expected 'Select'!"));
        result.registerAdvancement();
        parser.advance();
        
        // Check Expression
        AbstractValuedNode checkExpression = (AbstractValuedNode) result.register(GrammarRules.EXPRESSION.build(parser));
        if (result.error() != null) return result;
        
        // Newline
        if (parser.getCurrentToken().type() != TokenType.NEWLINE) return result.failure(new SyntaxException(parser, "Expected newline!"));
        result.registerAdvancement();
        parser.advance();
        
        // Build First Case
        BranchNode switchRootNode = (BranchNode) result.register(caseClause(parser, caseIndent, checkExpression));
        if (result.error() != null) return result;
        
        // Optional Additional Cases
        while (parser.getCurrentToken().type() == TokenType.INDENT && parser.peekNextToken().type() == TokenType.CASE)
        {
            BranchNode caseNode = (BranchNode) result.register(caseClause(parser, caseIndent, checkExpression));
            if (result.error() != null) return result;
            else switchRootNode.addElseIf(caseNode);
        }
    
        // Default Indent
        if (!parser.getCurrentToken().matches(TokenType.INDENT, caseIndent)) return result.failure(new SyntaxException(parser, "Expected indent of size " + caseIndent + "!"));
        result.registerAdvancement();
        parser.advance();
        
        // Default Keyword
        Token defaultKeyword = parser.getCurrentToken();
        if (defaultKeyword.type() != TokenType.DEFAULT) return result.failure(new SyntaxException(parser, "Expected 'Default:'!"));
        result.registerAdvancement();
        parser.advance();
    
        // Newline
        if (parser.getCurrentToken().type() != TokenType.NEWLINE) return result.failure(new SyntaxException(parser, "Expected newline!"));
        result.registerAdvancement();
        parser.advance();
        
        // Default Body
        AbstractNode defaultBody = result.register(GrammarRules.block(parser, caseIndent + 1));
        if (result.error() != null) return result;
        else switchRootNode.setElse(defaultBody);
        
        // End Statement
        Result<Token> closeToken = result.registerIssues(GrammarRules.endStatement(parser, caseIndent - 1, "Select"));
        if (result.error() != null) return result;
        
        switchRootNode.setEnd(closeToken.get());
        return result.success(switchRootNode);
    }
    
    private Result<AbstractNode> caseClause(Parser parser, int indent, AbstractValuedNode checkExpression)
    {
        Result<AbstractNode> result = new Result<>();
        
        // Indent
        if (!parser.getCurrentToken().matches(TokenType.INDENT, indent)) return result.failure(new SyntaxException(parser, "Expected indent of size " + indent + "!"));
        result.registerAdvancement();
        parser.advance();
        
        // Case Keyword
        Token caseKeyword = parser.getCurrentToken();
        if (caseKeyword.type() != TokenType.CASE) return result.failure(new SyntaxException(parser, "Expected 'Case'!"));
        result.registerAdvancement();
        parser.advance();
        
        // Test Expression
        AbstractValuedNode testExpression = (AbstractValuedNode) result.register(GrammarRules.EXPRESSION.build(parser));
        if (result.error() != null) return result;
        
        // Colon
        if (parser.getCurrentToken().type() != TokenType.COLON) return result.failure(new SyntaxException(parser, "Expected ':'!"));
        result.registerAdvancement();
        parser.advance();
        
        // Newline
        if (parser.getCurrentToken().type() != TokenType.NEWLINE) return result.failure(new SyntaxException(parser, "Expected newline!"));
        result.registerAdvancement();
        parser.advance();
        
        // Block
        AbstractNode body = result.register(GrammarRules.block(parser, indent + 1));
        if (result.error() != null) return result;
        
        // Create Branch
        return result.success(new BranchNode(caseKeyword, caseCheck(checkExpression, testExpression), body));
    }
    
    private BinaryOpNode caseCheck(AbstractValuedNode checkExpression, AbstractValuedNode testExpression)
    {
        return new BinaryOpNode(checkExpression, new Token(TokenType.EQUALS, null, testExpression.start(), testExpression.end()), testExpression);
    }
}