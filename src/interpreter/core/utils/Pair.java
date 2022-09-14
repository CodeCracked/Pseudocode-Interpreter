package interpreter.core.utils;

import java.util.Objects;

public class Pair<T1, T2>
{
    public T1 left;
    public T2 right;
    
    private Pair(T1 left, T2 right)
    {
        this.left = left;
        this.right = right;
    }
    
    public static <T1, T2> Pair<T1, T2> of(T1 left, T2 right)
    {
        return new Pair<>(left, right);
    }
    
    //region Object Overrides
    @Override
    public String toString()
    {
        return "Pair{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(left, pair.left) && Objects.equals(right, pair.right);
    }
    @Override
    public int hashCode()
    {
        return Objects.hash(left, right);
    }
    //endregion
}
