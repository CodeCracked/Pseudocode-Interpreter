package interpreter.core.runtime;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable
{
    private final Map<Enum<?>, Map<String, Symbol>> symbolMap;
    
    public SymbolTable()
    {
        this.symbolMap = new HashMap<>();
    }
    
    public boolean tryAddSymbol(Symbol symbol)
    {
        Map<String, Symbol> symbols = symbolMap.computeIfAbsent(symbol.type, k -> new HashMap<>());
        if (symbols.containsKey(symbol.name)) return false;
        else
        {
            symbols.put(symbol.name, symbol);
            return true;
        }
    }
    public Symbol getSymbol(Enum<?> type, String name)
    {
        if (!symbolMap.containsKey(type)) return null;
        else
        {
            Map<String, Symbol> symbols = symbolMap.get(type);
            return symbols.getOrDefault(name, null);
        }
    }
}
