package interpreter.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Result<T>
{
    private T value;
    private Exception error;
    private int advanceCount = 0;
    private List<Exception> warnings;
    
    public Result()
    {
        this.value = null;
        this.error = null;
        this.warnings = new ArrayList<>();
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
        registerIssues(result);
        return result.value;
    }
    public <T1> Result<T1> registerIssues(Result<T1> result)
    {
        advanceCount += result.advanceCount;
        warnings.addAll(result.warnings);
        if (result.error != null) error = result.error;
        return result;
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
    public Result<T> warn(Exception warning)
    {
        this.warnings.add(warning);
        return this;
    }
    public Result<T> failure(Exception error)
    {
        if (this.error == null || advanceCount == 0) this.error = error;
        return this;
    }
    
    public T get() { return value; }
    public List<Exception> warnings() { return Collections.unmodifiableList(warnings); }
    public Exception error() { return error; }

    public void displayIssues()
    {
        for (Exception warning : warnings)
        {
            IO.Warnings.println(warning.getMessage());
            warning.printStackTrace();
        }
        if (error != null)
        {
            IO.Errors.println(error.getMessage());
            error.printStackTrace();
        }
    }
}
