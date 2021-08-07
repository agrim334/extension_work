package org.codehaus.plexus.util.cli;

import java.io.File;
import java.io.PrintStream;

public class Commandline$Argument
  implements Arg
{
  private String[] parts;
  
  public void setValue(String value)
  {
    if (value != null) {
      parts = new String[] { value };
    }
  }
  
  public void setLine(String line)
  {
    if (line == null) {
      return;
    }
    try
    {
      parts = CommandLineUtils.translateCommandline(line);
    }
    catch (Exception e)
    {
      System.err.println("Error translating Commandline.");
    }
  }
  
  public void setFile(File value)
  {
    parts = new String[] { value.getAbsolutePath() };
  }
  
  public String[] getParts()
  {
    return parts;
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.cli.Commandline.Argument
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */