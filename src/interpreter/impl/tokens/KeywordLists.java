package interpreter.impl.tokens;

import java.util.List;

public class KeywordLists
{
    public static List<String> typeKeywords = List.of(
            "String",
            "Integer",
            "Real"
    );
    
    public static List<String> operatorKeywords = List.of(
            "="
    );
    
    public static List<String> statementKeywords = List.of(
            "Declare",
            "Display"
    );
}
