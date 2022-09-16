package main;

import java.util.function.Consumer;

public class VersionChecker
{
    public enum Result
    {
        BEHIND,
        UP_TO_DATE,
        AHEAD,
        FAILED
    }
    
    public static void run(Consumer<Result> callback)
    {
        Thread thread = new Thread(() ->
        {
            Version currentVersion = Version.getCurrentVersion();
            Version latestVersion = Version.getLatestVersion();
            
            if (currentVersion == null || latestVersion == null) callback.accept(Result.FAILED);
            else
            {
                int comparison = currentVersion.compareTo(latestVersion);
                if (comparison < 0) callback.accept(Result.BEHIND);
                else if (comparison == 0) callback.accept(Result.UP_TO_DATE);
                else callback.accept(Result.AHEAD);
            }
        });
        thread.setName("VersionChecker");
        thread.start();
    }
}
