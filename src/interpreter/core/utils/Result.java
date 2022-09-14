package interpreter.core.utils;

public class Result<T>
{
    private T value;
    private Exception error;
    private int advanceCount = 0;
    
    public Result()
    {
        this.value = null;
        this.error = null;
    }
    
    public static <T> Result<T> of(T value)
    {
        Result<T> result = new Result<>();
        return result.success(value);
    }
    public static <T> Result<T> fail(Exception e)
    {
        Result<T> result = new Result<>();
        return result.failure(e);
    }
    
    public T register(Result<T> result)
    {
        advanceCount += result.advanceCount;
        if (result.error != null) error = result.error;
        return result.value;
    }
    public void registerAdvancement()
    {
        advanceCount++;
    }
    public Result<T> success(T value)
    {
        this.value = value;
        return this;
    }
    public Result<T> failure(Exception error)
    {
        if (this.error == null || advanceCount == 0) this.error = error;
        return this;
    }
    
    public T get() { return value; }
    public Exception error() { return error; }
}
