package interpreter.impl.tokens;

import java.util.List;

public class KeywordLists
{
    public static List<String> typeKeywords = List.of(
            "String",
            "Integer",
            "Real",
            "Boolean"
    );
    
    public static List<String> statementKeywords = List.of(
            "Constant",
            "Declare",
            "Display",
            "Set",
            "Input",
            "Call",
            "If",
            "Select",
            "While",
            "Do"
    );
}
