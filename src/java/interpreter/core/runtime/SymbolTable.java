package interpreter.core.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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
    public <T> T getSymbol(Enum<?> type, String name)
    {
        if (!symbolMap.containsKey(type)) return null;
        else
        {
            Map<String, Symbol> symbols = symbolMap.get(type);
            if (symbols.containsKey(name)) return (T)symbols.get(name);
            else return null;
        }
    }
    
    public SymbolTable createChild()
    {
        return createChild(Symbol::clone);
    }
    public SymbolTable createChild(Function<Symbol, Symbol> childTransformer)
    {
        SymbolTable child = new SymbolTable();
        for (Map.Entry<Enum<?>, Map<String, Symbol>> symbolMapEntry : symbolMap.entrySet())
        {
            Map<String, Symbol> clonedSymbolMapEntry = new HashMap<>();
            for (Map.Entry<String, Symbol> symbolEntry : symbolMapEntry.getValue().entrySet()) clonedSymbolMapEntry.put(symbolEntry.getKey(), childTransformer.apply(symbolEntry.getValue()));
            child.symbolMap.put(symbolMapEntry.getKey(), clonedSymbolMapEntry);
        }
        return child;
    }
}
