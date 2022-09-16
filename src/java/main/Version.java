package main;

import interpreter.core.utils.IO;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;
import java.util.Scanner;

public class Version implements Comparable<Version>
{
    public final int majorVersion;
    public final int minorVersion;
    
    public Version(int majorVersion, int minorVersion)
    {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }
    
    public static Version getLatestVersion()
    {
        try
        {
            URL url = new URL("https://raw.githubusercontent.com/CodeCracked/Pseudocode-Interpreter/master/src/resources/version.txt");
            Scanner scanner = new Scanner(url.openStream());
            return parse(scanner.nextLine());
        }
        catch (Exception e)
        {
            IO.Errors.println("Failed to get latest interpreter version!");
            e.printStackTrace();
            return null;
        }
    }
    public static Version getCurrentVersion()
    {
        try (InputStream inputStream = Version.class.getResourceAsStream("/version.txt");
             InputStreamReader streamReader = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(streamReader)
        )
        {
            return parse(reader.readLine());
        }
        catch (Exception e)
        {
            IO.Errors.println("Failed to get interpreter version!");
            e.printStackTrace();
            return null;
        }
    }
    public static Version parse(String version)
    {
        String[] tokens = version.split("\\.");
        if (tokens.length != 2) return null;
        else
        {
            try
            {
                int majorVersion = Integer.parseInt(tokens[0]);
                int minorVersion = Integer.parseInt(tokens[1]);
                return new Version(majorVersion, minorVersion);
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();
                return null;
            }
        }
    }
    
    @Override
    public int compareTo(Version o)
    {
        int comparison = Integer.compare(majorVersion, o.majorVersion);
        if (comparison == 0) comparison = Integer.compare(minorVersion, o.minorVersion);
        return comparison;
    }
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return majorVersion == version.majorVersion && minorVersion == version.minorVersion;
    }
    @Override
    public int hashCode()
    {
        return Objects.hash(majorVersion, minorVersion);
    }
    
    @Override
    public String toString()
    {
        return majorVersion + "." + minorVersion;
    }
}
