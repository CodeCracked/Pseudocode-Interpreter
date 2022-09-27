package interpreter.core.runtime;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable
{
    private final SymbolTable parent;
    private final Map<Enum<?>, Map<String, Symbol>> symbolMap;
    
    public SymbolTable()
    {
        this(null);
    }
    private SymbolTable(SymbolTable parent)
    {
        this.parent = parent;
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
        if (!symbolMap.containsKey(type))
        {
            if (parent == null) return null;
            else return parent.getSymbol(type, name);
        }
        else
        {
            Map<String, Symbol> symbols = symbolMap.get(type);
            if (symbols.containsKey(name)) return (T)symbols.get(name);
            else if (parent == null) return null;
            else return parent.getSymbol(type, name);
        }
    }
    
    public SymbolTable createChild()
    {
        return new SymbolTable(this);
    }
}
